package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {

    private final VertexBedwars plugin;

    public GameListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        BedwarsGame game = plugin.getGameManager().getGame(victim);
        if (game == null || game.getState() != GameState.IN_GAME) return;

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(false);

        Player killer = victim.getKiller();

        // Drop inventory on death (not armor)
        for (ItemStack item : victim.getInventory().getContents()) {
            if (item != null && !isArmor(item)) {
                victim.getWorld().dropItemNaturally(victim.getLocation(), item);
            }
        }

        game.handlePlayerDeath(victim, killer);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;

        if (game.getState() == GameState.WAITING || game.getState() == GameState.STARTING) {
            event.setCancelled(true);
        }

        // Void kill
        if (player.getLocation().getY() <= plugin.getConfig().getInt("settings.void-kill-height", -10)) {
            player.setHealth(0);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        BedwarsGame victimGame = plugin.getGameManager().getGame(victim);
        if (victimGame == null) return;

        // Friendly fire
        if (event.getDamager() instanceof Player attacker) {
            BedwarsGame attackerGame = plugin.getGameManager().getGame(attacker);
            if (victimGame == attackerGame && victimGame.getTeam(victim) == victimGame.getTeam(attacker)) {
                event.setCancelled(true);
                attacker.sendMessage(MessageUtil.color("&cNon puoi colpire i tuoi compagni di squadra!"));
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game != null && (game.getState() == GameState.WAITING || game.getState() == GameState.STARTING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;
        // Respawn is handled manually in BedwarsGame
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;
        event.setCancelled(true);

        BedwarsTeam team = game.getTeam(player);
        String prefix = team != null ? team.getColor() + "[" + team.getDisplayName() + "] " : "";
        String message = prefix + "§f" + player.getName() + "§7: §f" + event.getMessage();

        // Send to game players only
        game.broadcast(message);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null || game.getState() != GameState.IN_GAME) return;
        // Allow pickup - iron/gold auto-collects
    }

    private boolean isArmor(ItemStack item) {
        String name = item.getType().name();
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") ||
               name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");
    }
}
