package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class ShopListener implements Listener {

    private final VertexBedwars plugin;
    private final Map<UUID, String> openInventoryType = new HashMap<>();

    public ShopListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null || game.getState() != GameState.IN_GAME) return;

        event.setCancelled(true);

        if (villager.getScoreboardTags().contains("vertex_shop")) {
            Inventory inv = plugin.getShopManager().createShopInventory(player);
            player.openInventory(inv);
            openInventoryType.put(player.getUniqueId(), "shop");
        } else if (villager.getScoreboardTags().contains("vertex_upgrade")) {
            BedwarsTeam team = game.getTeam(player);
            Inventory inv = plugin.getShopManager().createUpgradeInventory(player, team);
            player.openInventory(inv);
            openInventoryType.put(player.getUniqueId(), "upgrade");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String type = openInventoryType.get(player.getUniqueId());
        if (type == null) return;

        String title = event.getView().getTitle();
        if (!title.contains("SHOP BEDWARS") && !title.contains("UPGRADE TEAM")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.CYAN_STAINED_GLASS_PANE) return;

        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;

        if ("shop".equals(type)) {
            plugin.getShopManager().handlePurchase(player, clicked, game);
        } else if ("upgrade".equals(type)) {
            handleUpgradePurchase(player, clicked, game);
        }
    }

    private void handleUpgradePurchase(Player player, ItemStack clicked, BedwarsGame game) {
        BedwarsTeam team = game.getTeam(player);
        if (team == null) return;
        TeamData data = game.getTeamDataMap().get(team);
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;
        String name = ChatColor.stripColor(meta.getDisplayName());

        switch (name) {
            case "Affilatura" -> {
                int level = data.getSharpnessLevel();
                int[] costs = {2, 4, 8, 16};
                if (level >= 4) { player.sendMessage(MessageUtil.color("&cGià al livello massimo!")); return; }
                if (!hasEmerald(player, costs[level])) { notEnough(player, "Diamanti"); return; }
                removeEmerald(player, costs[level]);
                data.setSharpnessLevel(level + 1);
                game.broadcast(MessageUtil.color(team.getColoredName() + " &ahal potenziato &eAffilatura &aal Tier " + (level + 1) + "!"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case "Protezione Armatura" -> {
                int level = data.getProtectionLevel();
                int[] costs = {2, 4, 8, 16};
                if (level >= 4) { player.sendMessage(MessageUtil.color("&cGià al livello massimo!")); return; }
                if (!hasEmerald(player, costs[level])) { notEnough(player, "Diamanti"); return; }
                removeEmerald(player, costs[level]);
                data.setProtectionLevel(level + 1);
                game.broadcast(MessageUtil.color(team.getColoredName() + " &aha potenziato &eProtezione &aal Tier " + (level + 1) + "!"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case "Forgia Migliorata" -> {
                int level = data.getForgeLevel();
                int[] costs = {2, 4, 8, 16};
                String[] names = {"Forgia di Ferro", "Forgia d'Oro", "Forgia di Smeraldo", "Forgia Fusa"};
                if (level >= 4) { player.sendMessage(MessageUtil.color("&cGià al livello massimo!")); return; }
                if (!hasEmerald(player, costs[level])) { notEnough(player, "Diamanti"); return; }
                removeEmerald(player, costs[level]);
                data.setForgeLevel(level + 1);
                game.broadcast(MessageUtil.color(team.getColoredName() + " &aha sbloccato &6" + names[level] + "&a!"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case "Alacrità" -> {
                int level = data.getHasteLevel();
                int[] costs = {2, 4};
                if (level >= 2) { player.sendMessage(MessageUtil.color("&cGià al livello massimo!")); return; }
                if (!hasEmerald(player, costs[level])) { notEnough(player, "Diamanti"); return; }
                removeEmerald(player, costs[level]);
                data.setHasteLevel(level + 1);
                game.broadcast(MessageUtil.color(team.getColoredName() + " &aha potenziato &eAlacrità &aal Tier " + (level + 1) + "!"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            case "Pool di Cura" -> {
                if (data.hasHealPool()) { player.sendMessage(MessageUtil.color("&cGià acquistato!")); return; }
                if (!hasEmerald(player, 1)) { notEnough(player, "Smeraldo"); return; }
                removeEmerald(player, 1);
                data.setHealPool(true);
                game.broadcast(MessageUtil.color(team.getColoredName() + " &aha sbloccato il &aPool di Cura&a!"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
        }
    }

    private boolean hasEmerald(Player player, int amount) {
        int count = 0;
        for (var item : player.getInventory().getContents())
            if (item != null && item.getType() == Material.EMERALD) count += item.getAmount();
        return count >= amount;
    }

    private void removeEmerald(Player player, int amount) {
        int toRemove = amount;
        for (int i = 0; i < player.getInventory().getSize() && toRemove > 0; i++) {
            var item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.EMERALD) {
                if (item.getAmount() <= toRemove) { toRemove -= item.getAmount(); player.getInventory().setItem(i, null); }
                else { item.setAmount(item.getAmount() - toRemove); toRemove = 0; }
            }
        }
    }

    private void notEnough(Player player, String currency) {
        player.sendMessage(MessageUtil.color("&cNon hai abbastanza &e" + currency + "&c!"));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
    }
}
