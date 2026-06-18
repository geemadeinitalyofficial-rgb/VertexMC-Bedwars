package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.*;

public class LobbySignManager implements Listener {

    private final VertexBedwars plugin;
    private final File signsFile;
    private YamlConfiguration signsConfig;
    // location string -> arenaName
    private final Map<String, String> signs = new HashMap<>();

    public LobbySignManager(VertexBedwars plugin) {
        this.plugin = plugin;
        this.signsFile = new File(plugin.getDataFolder(), "signs.yml");
        this.signsConfig = signsFile.exists() ?
                YamlConfiguration.loadConfiguration(signsFile) : new YamlConfiguration();
        loadSigns();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void loadSigns() {
        signs.clear();
        ConfigurationSection section = signsConfig.getConfigurationSection("signs");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            signs.put(key, section.getString(key));
        }
    }

    private void saveSigns() {
        for (Map.Entry<String, String> entry : signs.entrySet()) {
            signsConfig.set("signs." + entry.getKey(), entry.getValue());
        }
        try { signsConfig.save(signsFile); } catch (Exception e) { e.printStackTrace(); }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("vertexbedwars.admin")) return;

        String line0 = event.getLine(0);
        if (line0 == null || !line0.equalsIgnoreCase("[BedWars]")) return;

        String arenaName = event.getLine(1);
        if (arenaName == null || arenaName.isEmpty()) {
            player.sendMessage(MessageUtil.color("&cSpecifica il nome dell'arena sulla riga 2!"));
            return;
        }

        if (!plugin.getArenaManager().arenaExists(arenaName)) {
            player.sendMessage(MessageUtil.color("&cArena &e" + arenaName + " &cnon trovata!"));
            return;
        }

        String locKey = locToKey(event.getBlock().getLocation());
        signs.put(locKey, arenaName);
        saveSigns();

        event.setLine(0, MessageUtil.color("&b&l[BedWars]"));
        event.setLine(1, MessageUtil.color("&e" + arenaName));
        event.setLine(2, MessageUtil.color("&7In attesa..."));
        event.setLine(3, MessageUtil.color("&f0/?"));
        player.sendMessage(MessageUtil.color("&aCartello BedWars creato per l'arena &e" + arenaName + "&a!"));
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;

        Player player = event.getPlayer();
        String locKey = locToKey(event.getClickedBlock().getLocation());
        String arenaName = signs.get(locKey);
        if (arenaName == null) return;

        event.setCancelled(true);
        plugin.getGameManager().joinGame(player, arenaName);
    }

    /** Aggiorna tutti i cartelli di un'arena (da chiamare ogni tick o su eventi) */
    public void updateSigns(BedwarsGame game) {
        String arenaName = game.getArena().getName();
        for (Map.Entry<String, String> entry : signs.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase(arenaName)) continue;
            Location loc = keyToLoc(entry.getKey());
            if (loc == null) continue;
            Block block = loc.getBlock();
            if (!(block.getState() instanceof Sign sign)) continue;

            String statusColor;
            String statusText;
            switch (game.getState()) {
                case WAITING -> { statusColor = "&a"; statusText = "In attesa"; }
                case STARTING -> { statusColor = "&e"; statusText = "Inizia in " + game.getCountdownSeconds() + "s"; }
                case IN_GAME -> { statusColor = "&c"; statusText = "In corso"; }
                default -> { statusColor = "&7"; statusText = "Resettando..."; }
            }

            sign.getSide(Side.FRONT).setLine(0, MessageUtil.color("&b&l[BedWars]"));
            sign.getSide(Side.FRONT).setLine(1, MessageUtil.color("&e" + game.getArena().getDisplayName()));
            sign.getSide(Side.FRONT).setLine(2, MessageUtil.color(statusColor + statusText));
            sign.getSide(Side.FRONT).setLine(3, MessageUtil.color(
                    "&f" + game.getTotalPlayers() + "/" + game.getArena().getMaxPlayers()));
            sign.update();
        }
    }

    public void removeSign(Location loc) {
        String key = locToKey(loc);
        if (signs.remove(key) != null) {
            signsConfig.set("signs." + key, null);
            try { signsConfig.save(signsFile); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private String locToKey(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location keyToLoc(String key) {
        try {
            String[] parts = key.split(",");
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;
            return new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (Exception e) { return null; }
    }
}
