package it.vertex.bedwars.game;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.generators.ResourceGenerator;
import it.vertex.bedwars.managers.AntiCampingManager;
import it.vertex.bedwars.utils.DeathAnimationUtil;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BedwarsGame {

    private final VertexBedwars plugin;
    private final Arena arena;
    private GameState state;

    private final Map<UUID, BedwarsTeam> playerTeams = new HashMap<>();
    private final Map<BedwarsTeam, TeamData> teamDataMap = new HashMap<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final List<Entity> spawnedEntities = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();
    private final List<ResourceGenerator> generators = new ArrayList<>();

    private BukkitTask gameTask;
    private BukkitTask countdownTask;
    private int countdownSeconds;
    private int gameTimeSeconds = 0;

    private int diamondTier = 1;
    private int emeraldTier = 1;
    private static final int[] DIAMOND_UPGRADE_TIMES = {300, 600};
    private static final int[] EMERALD_UPGRADE_TIMES = {600};

    private final AntiCampingManager antiCamping = new AntiCampingManager();
    private final GameEndSummary endSummary;

    public BedwarsGame(VertexBedwars plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.state = GameState.WAITING;
        this.countdownSeconds = plugin.getConfig().getInt("settings.max-wait-time", 60);
        this.endSummary = new GameEndSummary(plugin);

        for (BedwarsTeam team : arena.getEnabledTeams()) {
            TeamData data = new TeamData(team);
            data.setSpawnLocation(arena.getTeamSpawns().get(team));
            data.setBedLocation(arena.getBedLocations().get(team));
            teamDataMap.put(team, data);
        }
    }

    // ── Lobby ────────────────────────────────────────────────────────────────────

    public boolean addPlayer(Player player) {
        if (playerTeams.size() >= arena.getMaxPlayers()) return false;
        BedwarsTeam assigned = assignTeam(player);
        if (assigned == null) return false;
        playerTeams.put(player.getUniqueId(), assigned);
        teamDataMap.get(assigned).addMember(player);
        teleportToLobby(player);
        applyLobbyEffects(player);
        broadcast(MessageUtil.color("&e" + player.getName() + " &7si è unito al team " +
                assigned.getColoredName() + " &7(" + getTotalPlayers() + "/" + arena.getMaxPlayers() + ")"));
        if (getTotalPlayers() >= plugin.getConfig().getInt("settings.min-players", 2) && state == GameState.WAITING)
            startCountdown();
        plugin.getScoreboardManager().updateLobbyBoard(this, player);
        return true;
    }

    public void removePlayer(Player player, boolean toHub) {
        BedwarsTeam team = playerTeams.remove(player.getUniqueId());
        if (team != null) {
            teamDataMap.get(team).removeMember(player);
            broadcast(MessageUtil.color("&e" + player.getName() + " &7ha lasciato la partita."));
        }
        spectators.remove(player.getUniqueId());
        plugin.getKillStreakManager().reset(player);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        restorePlayer(player);
        if (toHub) {
            Location hub = plugin.getConfig().getLocation("settings.lobby-spawn");
            if (hub != null) player.teleport(hub);
        }
        if (state == GameState.IN_GAME) checkWinCondition();
        if (state == GameState.STARTING && getTotalPlayers() < plugin.getConfig().getInt("settings.min-players", 2))
            cancelCountdown();
        plugin.getScoreboardManager().removePlayer(player);
    }

    private BedwarsTeam assignTeam(Player player) {
        BedwarsTeam smallest = null;
        int minSize = Integer.MAX_VALUE;
        for (BedwarsTeam team : arena.getEnabledTeams()) {
            TeamData data = teamDataMap.get(team);
            if (data.getMembers().size() < arena.getPlayersPerTeam() && data.getMembers().size() < minSize) {
                smallest = team; minSize = data.getMembers().size();
            }
        }
        return smallest;
    }

    // ── Countdown ────────────────────────────────────────────────────────────────

    public void startCountdown() {
        if (state != GameState.WAITING) return;
        state = GameState.STARTING;
        countdownSeconds = plugin.getConfig().getInt("settings.max-wait-time", 60);
        countdownTask = new BukkitRunnable() {
            @Override public void run() {
                if (countdownSeconds <= 0) { startGame(); cancel(); return; }
                if (countdownSeconds <= 5 || countdownSeconds % 10 == 0) {
                    broadcast(MessageUtil.color("&aLa partita inizia in &e" + countdownSeconds + " &asecondi!"));
                    for (Player p : getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f,
                                countdownSeconds <= 5 ? 2f : 1f);
                }
                countdownSeconds--;
                for (Player p : getOnlinePlayers())
                    plugin.getScoreboardManager().updateLobbyBoard(BedwarsGame.this, p);
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void cancelCountdown() {
        if (countdownTask != null) { countdownTask.cancel(); countdownTask = null; }
        state = GameState.WAITING;
        broadcast(MessageUtil.color("&cNon ci sono abbastanza giocatori. Countdown annullato."));
    }

    // ── Start ─────────────────────────────────────────────────────────────────────

    public void startGame() {
        if (countdownTask != null) { countdownTask.cancel(); countdownTask = null; }
        state = GameState.IN_GAME;
        spawnGenerators();
        spawnVillagers();
        for (Map.Entry<UUID, BedwarsTeam> entry : playerTeams.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            BedwarsTeam team = entry.getValue();
            player.teleport(teamDataMap.get(team).getSpawnLocation());
            equipPlayer(player, team);
            player.setGameMode(GameMode.SURVIVAL);
            plugin.getScoreboardManager().createGameBoard(this, player);
        }
        broadcast(MessageUtil.color("&a&lLA PARTITA È INIZIATA! Difendi il tuo letto!"));

        gameTask = new BukkitRunnable() {
            @Override public void run() {
                gameTimeSeconds++;
                tickGenerators();
                tickHealPools();
                tickGeneratorUpgrades();
                antiCamping.tick(BedwarsGame.this);
                // Update signs
                plugin.getLobbySignManager().updateSigns(BedwarsGame.this);
                for (Player p : getOnlinePlayers())
                    plugin.getScoreboardManager().updateGameBoard(BedwarsGame.this, p);
                // Sudden death announcements
                announceSuddenDeathWarning();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void announceSuddenDeathWarning() {
        int timeLeft = AntiCampingManager.SUDDEN_DEATH_TIME - gameTimeSeconds;
        if (timeLeft == 300 || timeLeft == 120 || timeLeft == 60 || timeLeft == 30) {
            broadcast(MessageUtil.color("&c⚠ Sudden Death tra &e" + timeLeft + " &csecondi!"));
        }
    }

    // ── Death / Respawn ───────────────────────────────────────────────────────────

    public void handlePlayerDeath(Player victim, Player killer) {
        BedwarsTeam victimTeam = playerTeams.get(victim.getUniqueId());
        TeamData data = teamDataMap.get(victimTeam);

        // Kill streak
        if (killer != null) {
            plugin.getKillStreakManager().onKill(killer, this);
            BedwarsTeam killerTeam = playerTeams.get(killer.getUniqueId());
            if (killerTeam != null) teamDataMap.get(killerTeam).addKill();
            plugin.getStatsManager().addKill(killer);
        }
        plugin.getKillStreakManager().onDeath(victim);

        boolean definitive = !data.hasBed();
        DeathAnimationUtil.playDeathEffect(victim, killer, victimTeam, definitive);

        victim.getInventory().clear();
        victim.setGameMode(GameMode.SPECTATOR);
        victim.teleport(data.getSpawnLocation());

        if (definitive) {
            data.removeMember(victim);
            playerTeams.remove(victim.getUniqueId());
            spectators.add(victim.getUniqueId());
            plugin.getStatsManager().addDeath(victim);

            String killerPart = killer != null ?
                    " &7per mano di " + (playerTeams.containsKey(killer.getUniqueId()) ?
                                         playerTeams.get(killer.getUniqueId()).getColor() + killer.getName() :
                            killer.getName()) : "";
            broadcast(MessageUtil.color(
                    victimTeam.getColor() + "✘ &r" + victimTeam.getColoredName() + " &7» " +
                    "&f" + victim.getName() + " &7è stato eliminato definitivamente" + killerPart + "!"));
            checkWinCondition();
        } else {
            int respawnTime = plugin.getConfig().getInt("settings.respawn-time", 5);
            plugin.getStatsManager().addDeath(victim);
            new BukkitRunnable() {
                int t = respawnTime;
                @Override public void run() {
                    if (t <= 0) { respawnPlayer(victim); cancel(); return; }
                    victim.sendMessage(MessageUtil.color("&eTornerai in vita tra &c" + t + " &esecondi..."));
                    victim.playSound(victim.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
                    t--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    private void respawnPlayer(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) return;
        BedwarsTeam team = playerTeams.get(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(teamDataMap.get(team).getSpawnLocation());
        player.setHealth(20.0);
        player.setFoodLevel(20);
        equipPlayer(player, team);
        applyTeamUpgradeEffects(player, team);
        player.sendTitle(MessageUtil.color("&a&lRIAPPARIZIONE!"), "", 5, 30, 10);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
    }

    // ── Bed Destroyed ─────────────────────────────────────────────────────────────

    public void handleBedDestroyed(BedwarsTeam team, Player destroyer) {
        teamDataMap.get(team).destroyBed();
        if (destroyer != null) plugin.getStatsManager().addBedDestroyed(destroyer);

        Location bedLoc = arena.getBedLocations().get(team);
        DeathAnimationUtil.playBedDestroyEffect(bedLoc, team);

        String destroyerInfo = "";
        if (destroyer != null) {
            BedwarsTeam dt = playerTeams.get(destroyer.getUniqueId());
            destroyerInfo = " &7per mano di " + (dt != null ? dt.getColor() : "") + destroyer.getName();
        }

        for (Player p : getOnlinePlayers()) {
            p.sendMessage(MessageUtil.color(
                    team.getColor() + "■ &r&cLetto di " + team.getColoredName() +
                    " &cdistrutto" + destroyerInfo + "&c!"));
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1f);
        }
        for (UUID uid : teamDataMap.get(team).getMembers()) {
            Player p = Bukkit.getPlayer(uid);
            if (p != null) {
                p.sendTitle(MessageUtil.color("&c&lLETTO DISTRUTTO!"),
                        MessageUtil.color("&eNon riapparairai dopo la morte!"), 10, 80, 20);
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
            }
        }
    }

    // ── Win ───────────────────────────────────────────────────────────────────────

    public void checkWinCondition() {
        List<BedwarsTeam> alive = getAliveTeams();
        if (alive.size() <= 1) endGame(alive.isEmpty() ? null : alive.get(0));
    }

    private void endGame(BedwarsTeam winner) {
        state = GameState.ENDING;
        if (gameTask != null) { gameTask.cancel(); gameTask = null; }
        for (ResourceGenerator gen : generators) gen.stop();

        endSummary.sendSummary(this, winner);

        for (Player p : getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            if (winner != null) {
                p.sendTitle(
                        winner.getColoredName() + MessageUtil.color(" &6VITTORIA!"),
                        MessageUtil.color("&7Verrai teletrasportato tra 10 secondi"), 10, 120, 20);
            }
        }

        if (winner != null) {
            for (UUID uid : teamDataMap.get(winner).getMembers()) {
                Player p = Bukkit.getPlayer(uid);
                if (p != null) plugin.getStatsManager().addWin(p);
            }
        }
        // Tutti gli altri: sconfitta
        for (Map.Entry<UUID, BedwarsTeam> entry : playerTeams.entrySet()) {
            if (winner != null && entry.getValue() == winner) continue;
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null) plugin.getStatsManager().addLoss(p);
        }

        plugin.getLobbySignManager().updateSigns(this);

        new BukkitRunnable() {
            @Override public void run() { reset(); }
        }.runTaskLater(plugin, 200L);
    }

    // ── Generators ────────────────────────────────────────────────────────────────

    private void spawnGenerators() {
        for (BedwarsTeam team : arena.getEnabledTeams()) {
            Location spawn = arena.getTeamSpawns().get(team);
            if (spawn == null) continue;
            generators.add(new ResourceGenerator(plugin, this, spawn, ResourceGenerator.Type.IRON, team));
            generators.add(new ResourceGenerator(plugin, this, spawn, ResourceGenerator.Type.GOLD, team));
        }
        for (Location loc : arena.getDiamondGenerators())
            generators.add(new ResourceGenerator(plugin, this, loc, ResourceGenerator.Type.DIAMOND, null));
        for (Location loc : arena.getEmeraldGenerators())
            generators.add(new ResourceGenerator(plugin, this, loc, ResourceGenerator.Type.EMERALD, null));
        for (ResourceGenerator gen : generators) gen.start();
    }

    private void tickGenerators() { for (ResourceGenerator gen : generators) gen.tick(); }

    private void tickGeneratorUpgrades() {
        if (diamondTier == 1 && gameTimeSeconds >= DIAMOND_UPGRADE_TIMES[0]) {
            diamondTier = 2;
            broadcast(MessageUtil.color("&b&l[◆] &bGeneratori di Diamante → &fTier II&b!"));
        } else if (diamondTier == 2 && gameTimeSeconds >= DIAMOND_UPGRADE_TIMES[1]) {
            diamondTier = 3;
            broadcast(MessageUtil.color("&b&l[◆] &bGeneratori di Diamante → &fTier III&b!"));
        }
        if (emeraldTier == 1 && gameTimeSeconds >= EMERALD_UPGRADE_TIMES[0]) {
            emeraldTier = 2;
            broadcast(MessageUtil.color("&a&l[◈] &aGeneratori di Smeraldo → &fTier II&a!"));
        }
    }

    private void tickHealPools() {
        for (Map.Entry<UUID, BedwarsTeam> entry : playerTeams.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            TeamData data = teamDataMap.get(entry.getValue());
            if (!data.hasHealPool()) continue;
            Location spawn = data.getSpawnLocation();
            if (spawn != null && player.getLocation().distance(spawn) <= 10 && player.getHealth() < 20)
                player.setHealth(Math.min(player.getHealth() + 1, 20));
        }
    }

    // ── Villagers ─────────────────────────────────────────────────────────────────

    private void spawnVillagers() {
        for (Location loc : arena.getShopVillagers()) {
            Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
            v.setCustomName(MessageUtil.color("&e&lSHOP"));
            v.setCustomNameVisible(true); v.setAI(false); v.setInvulnerable(true);
            v.setSilent(true); v.setProfession(Villager.Profession.TOOLSMITH);
            v.addScoreboardTag("vertex_shop");
            spawnedEntities.add(v);
        }
        for (Location loc : arena.getUpgradeVillagers()) {
            Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
            v.setCustomName(MessageUtil.color("&a&lUPGRADES"));
            v.setCustomNameVisible(true); v.setAI(false); v.setInvulnerable(true);
            v.setSilent(true); v.setProfession(Villager.Profession.ARMORER);
            v.addScoreboardTag("vertex_upgrade");
            spawnedEntities.add(v);
        }
    }

    // ── Equipment ─────────────────────────────────────────────────────────────────

    private void equipPlayer(Player player, BedwarsTeam team) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
        player.getInventory().addItem(new ItemStack(team.getWoolMaterial(), 16));
        plugin.getShopManager().giveColoredArmor(player, team);
        player.setHealth(20.0); player.setFoodLevel(20); player.setSaturation(20f);
    }

    private void applyTeamUpgradeEffects(Player player, BedwarsTeam team) {
        TeamData data = teamDataMap.get(team);
        player.removePotionEffect(PotionEffectType.HASTE);
        if (data.getHasteLevel() > 0)
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,
                    Integer.MAX_VALUE, data.getHasteLevel() - 1, true, false));
    }

    private void teleportToLobby(Player player) {
        Location lobby = arena.getLobbySpawn();
        if (lobby != null) player.teleport(lobby);
    }

    private void applyLobbyEffects(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0); player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
    }

    private void restorePlayer(Player player) {
        player.setHealth(20.0); player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
        player.getInventory().clear();
    }

    // ── Reset ─────────────────────────────────────────────────────────────────────

    public void reset() {
        state = GameState.RESETTING;
        for (Entity e : spawnedEntities) e.remove();
        spawnedEntities.clear();
        for (Location loc : placedBlocks) loc.getBlock().setType(Material.AIR);
        placedBlocks.clear();
        for (Map.Entry<BedwarsTeam, Location> entry : arena.getBedLocations().entrySet()) {
            Location loc = entry.getValue();
            if (loc != null) loc.getBlock().setType(entry.getKey().getBedMaterial());
        }
        Location hub = plugin.getConfig().getLocation("settings.lobby-spawn");
        for (Player p : getOnlinePlayers()) {
            p.getInventory().clear(); p.setGameMode(GameMode.SURVIVAL);
            if (hub != null) p.teleport(hub);
            plugin.getScoreboardManager().removePlayer(p);
        }
        playerTeams.clear(); spectators.clear(); generators.clear();
        antiCamping.reset();
        plugin.getTrapManager().reset();
        plugin.getKillStreakManager().resetAll();
        for (BedwarsTeam team : arena.getEnabledTeams()) {
            TeamData data = new TeamData(team);
            data.setSpawnLocation(arena.getTeamSpawns().get(team));
            data.setBedLocation(arena.getBedLocations().get(team));
            teamDataMap.put(team, data);
        }
        gameTimeSeconds = 0; diamondTier = 1; emeraldTier = 1;
        state = GameState.WAITING;
        plugin.getGameManager().onGameReset(this);
        plugin.getLobbySignManager().updateSigns(this);
    }

    // ── Utility ───────────────────────────────────────────────────────────────────

    public void broadcast(String message) {
        for (Player p : getOnlinePlayers()) p.sendMessage(message);
        for (UUID uid : spectators) { Player p = Bukkit.getPlayer(uid); if (p != null) p.sendMessage(message); }
    }

    public List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uid : playerTeams.keySet()) { Player p = Bukkit.getPlayer(uid); if (p != null) list.add(p); }
        return list;
    }

    public List<BedwarsTeam> getAliveTeams() {
        List<BedwarsTeam> alive = new ArrayList<>();
        for (Map.Entry<BedwarsTeam, TeamData> e : teamDataMap.entrySet())
            if (!e.getValue().isEliminated()) alive.add(e.getKey());
        return alive;
    }

    public int getTotalPlayers()                             { return playerTeams.size(); }
    public Arena getArena()                                  { return arena; }
    public GameState getState()                              { return state; }
    public Map<UUID, BedwarsTeam> getPlayerTeams()          { return playerTeams; }
    public Map<BedwarsTeam, TeamData> getTeamDataMap()      { return teamDataMap; }
    public BedwarsTeam getTeam(Player player)               { return playerTeams.get(player.getUniqueId()); }
    public boolean isInGame(Player player)                   { return playerTeams.containsKey(player.getUniqueId()); }
    public boolean isSpectator(Player player)               { return spectators.contains(player.getUniqueId()); }
    public List<Location> getPlacedBlocks()                 { return placedBlocks; }
    public int getDiamondTier()                             { return diamondTier; }
    public int getEmeraldTier()                             { return emeraldTier; }
    public int getGameTimeSeconds()                         { return gameTimeSeconds; }
    public int getCountdownSeconds()                        { return countdownSeconds; }
    public AntiCampingManager getAntiCamping()              { return antiCamping; }
}
