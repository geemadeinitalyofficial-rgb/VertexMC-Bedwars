package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.BedwarsTeam;
import it.vertex.bedwars.utils.ItemBuilder;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

public class ShopManager {

    private final VertexBedwars plugin;

    public ShopManager(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    public Inventory createShopInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.color("&8&lSHOP BEDWARS"));

        // ── BLOCCHI (row 0) ──────────────────────────────────────────────────
        inv.setItem(0, createShopItem("&f&lBLOCCHI", null, true));
        inv.setItem(1, ItemBuilder.named(Material.OAK_PLANKS, "&fBlocci di legno &7x16")
                .lore("&7Costo: &62 Ferro").build());
        inv.setItem(2, ItemBuilder.named(Material.TERRACOTTA, "&fTerracotta &7x16")
                .lore("&7Costo: &64 Ferro").build());
        inv.setItem(3, ItemBuilder.named(Material.END_STONE, "&fPietra del Nether &7x4")
                .lore("&7Costo: &62 Ferro").build());
        inv.setItem(4, ItemBuilder.named(Material.LADDER, "&fScale &7x4")
                .lore("&7Costo: &62 Ferro").build());
        inv.setItem(5, ItemBuilder.named(Material.OBSIDIAN, "&fOsservatorio &7x4")
                .lore("&7Costo: &b4 Diamanti").build());
        inv.setItem(6, ItemBuilder.named(Material.GLASS, "&fVetro esplosivo &7x4")
                .lore("&7Costo: &62 Ferro").build());

        // ── ARMI (row 1) ─────────────────────────────────────────────────────
        inv.setItem(9, createShopItem("&c&lARMI", null, true));
        inv.setItem(10, ItemBuilder.named(Material.STONE_SWORD, "&fSpada di pietra")
                .lore("&7Costo: &610 Ferro").build());
        inv.setItem(11, ItemBuilder.named(Material.IRON_SWORD, "&fSpada di ferro")
                .lore("&7Costo: &67 Oro").build());
        inv.setItem(12, ItemBuilder.named(Material.DIAMOND_SWORD, "&fSpada di diamante")
                .lore("&7Costo: &b3 Diamanti").build());
        inv.setItem(13, ItemBuilder.named(Material.BOW, "&fArco")
                .lore("&7Costo: &63 Oro").build());
        inv.setItem(14, ItemBuilder.named(Material.ARROW, "&fFrecce &7x8")
                .lore("&7Costo: &62 Oro").build());
        inv.setItem(15, ItemBuilder.named(Material.BOW, "&fArco con Potenza I")
                .enchant(Enchantment.POWER, 1).lore("&7Costo: &b2 Diamanti").build());
        inv.setItem(16, ItemBuilder.named(Material.BOW, "&fArco con Potenza I e Proiettile IV")
                .enchant(Enchantment.POWER, 1).enchant(Enchantment.PUNCH, 1)
                .lore("&7Costo: &b6 Diamanti").build());

        // ── ARMATURE (row 2) ─────────────────────────────────────────────────
        inv.setItem(18, createShopItem("&b&lARMATURE", null, true));
        inv.setItem(19, ItemBuilder.named(Material.CHAINMAIL_BOOTS, "&fStivali di cotta")
                .lore("&7Costo: &640 Ferro").build());
        inv.setItem(20, ItemBuilder.named(Material.CHAINMAIL_LEGGINGS, "&fPantaloni di cotta")
                .lore("&7Costo: &63 Oro").build());
        inv.setItem(21, ItemBuilder.named(Material.IRON_BOOTS, "&fStivali di ferro")
                .lore("&7Costo: &67 Oro").build());
        inv.setItem(22, ItemBuilder.named(Material.IRON_LEGGINGS, "&fPantaloni di ferro")
                .lore("&7Costo: &b4 Diamanti").build());
        inv.setItem(23, ItemBuilder.named(Material.DIAMOND_BOOTS, "&fStivali di diamante")
                .lore("&7Costo: &b6 Diamanti").build());
        inv.setItem(24, ItemBuilder.named(Material.DIAMOND_LEGGINGS, "&fPantaloni di diamante")
                .lore("&7Costo: &b8 Diamanti").build());

        // ── STRUMENTI (row 3) ────────────────────────────────────────────────
        inv.setItem(27, createShopItem("&e&lSTRUMENTI", null, true));
        inv.setItem(28, ItemBuilder.named(Material.WOODEN_PICKAXE, "&fPiccone di legno")
                .lore("&7Costo: &fGratuito").build());
        inv.setItem(29, ItemBuilder.named(Material.STONE_PICKAXE, "&fPiccone di pietra")
                .lore("&7Costo: &610 Ferro").build());
        inv.setItem(30, ItemBuilder.named(Material.IRON_PICKAXE, "&fPiccone di ferro")
                .lore("&7Costo: &65 Oro").build());
        inv.setItem(31, ItemBuilder.named(Material.DIAMOND_PICKAXE, "&fPiccone di diamante")
                .lore("&7Costo: &b3 Diamanti").build());
        inv.setItem(32, ItemBuilder.named(Material.IRON_AXE, "&fAscia di ferro")
                .lore("&7Costo: &63 Oro").build());
        inv.setItem(33, ItemBuilder.named(Material.SHEARS, "&fForbici")
                .lore("&7Costo: &620 Ferro").build());

        // ── SPECIALI (row 4) ─────────────────────────────────────────────────
        inv.setItem(36, createShopItem("&d&lSPECIALI", null, true));
        inv.setItem(37, ItemBuilder.named(Material.GOLDEN_APPLE, "&fMela d'oro")
                .lore("&7Costo: &63 Oro").build());
        inv.setItem(38, ItemBuilder.named(Material.ENDER_PEARL, "&fPerla dell'Ender")
                .lore("&7Costo: &b4 Diamanti").build());
        inv.setItem(39, ItemBuilder.named(Material.TNT, "&fTNT")
                .lore("&7Costo: &b4 Diamanti").build());
        inv.setItem(40, ItemBuilder.named(Material.FIRE_CHARGE, "&fPalla di fuoco")
                .lore("&7Costo: &640 Ferro").build());
        inv.setItem(41, ItemBuilder.named(Material.COBWEB, "&fRagnatela")
                .lore("&7Costo: &64 Oro").build());
        inv.setItem(42, ItemBuilder.named(Material.FLINT_AND_STEEL, "&fAcciarino")
                .lore("&7Costo: &64 Oro").build());
        inv.setItem(43, ItemBuilder.named(Material.POTION, "&fPozione della velocità II")
                .lore("&7Costo: &b2 Diamanti").build());

        return inv;
    }

    public Inventory createUpgradeInventory(Player player, BedwarsTeam team) {
        Inventory inv = Bukkit.createInventory(null, 45, MessageUtil.color("&8&lUPGRADE TEAM"));

        // ── AFFILATURA (Sharpness) ────────────────────────────────────────────
        inv.setItem(10, ItemBuilder.named(Material.DIAMOND_SWORD, "&b&lAffilatura")
                .lore("&7Aggiunge Affilatura alle spade del team.",
                      "", "&eTier I &8- &62 Diamanti",
                      "&eTier II &8- &64 Diamanti",
                      "&eTier III &8- &68 Diamanti",
                      "&eTier IV &8- &616 Diamanti").build());

        // ── PROTEZIONE (Protection) ───────────────────────────────────────────
        inv.setItem(11, ItemBuilder.named(Material.IRON_CHESTPLATE, "&b&lProtezione Armatura")
                .lore("&7Aggiunge Protezione a tutta l'armatura del team.",
                      "", "&eTier I &8- &62 Diamanti",
                      "&eTier II &8- &64 Diamanti",
                      "&eTier III &8- &68 Diamanti",
                      "&eTier IV &8- &616 Diamanti").build());

        // ── FORGIA (Forge) ────────────────────────────────────────────────────
        inv.setItem(12, ItemBuilder.named(Material.FURNACE, "&6&lForgia Migliorata")
                .lore("&7Aumenta la velocità di produzione dei generatori del tuo team.",
                      "", "&eTier I &8(Forgia di Ferro) &8- &62 Diamanti",
                      "&eTier II &8(Forgia d'Oro) &8- &64 Diamanti",
                      "&eTier III &8(Forgia di Smeraldo) &8- &68 Diamanti",
                      "&eTier IV &8(Forgia Fusa) &8- &616 Diamanti").build());

        // ── ALACRITÀ (Haste) ─────────────────────────────────────────────────
        inv.setItem(13, ItemBuilder.named(Material.GOLDEN_PICKAXE, "&e&lAlacrità")
                .lore("&7Aggiunge Alacrità ai giocatori del team.",
                      "", "&eTier I &8- &62 Diamanti",
                      "&eTier II &8- &64 Diamanti").build());

        // ── POOL DI CURA (Heal Pool) ──────────────────────────────────────────
        inv.setItem(14, ItemBuilder.named(Material.BEACON, "&a&lPool di Cura")
                .lore("&7Rigenerazione lenta ai tuoi giocatori vicino alla base.",
                      "", "&e1 livello &8- &61 Smeraldo").build());

        // ── TRAPPOLA (Trap) ───────────────────────────────────────────────────
        inv.setItem(20, ItemBuilder.named(Material.TRIPWIRE_HOOK, "&c&lTrappola del Pronto Soccorso")
                .lore("&7Rimozione cecità e rallentamento istantanea a chi entra.",
                      "", "&e1 utilizzo &8- &61 Smeraldo").build());

        inv.setItem(21, ItemBuilder.named(Material.PISTON, "&c&lTrappola con Disturbo Minatore")
                .lore("&7Infligge Disturbo Minatore a chi entra nella base.",
                      "", "&e1 utilizzo &8- &61 Smeraldo").build());

        inv.setItem(22, ItemBuilder.named(Material.FEATHER, "&c&lTrappola con Allarme")
                .lore("&7Avvisa il team quando un nemico entra nella base.",
                      "", "&e1 utilizzo &8- &61 Smeraldo").build());

        return inv;
    }

    /**
     * Handle a shop purchase. Returns true if purchase was successful.
     */
    public boolean handlePurchase(Player player, ItemStack clicked, it.vertex.bedwars.game.BedwarsGame game) {
        if (clicked == null || clicked.getType() == Material.AIR) return false;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;

        String name = ChatColor.stripColor(meta.getDisplayName());
        ShopItem shopItem = getShopItem(name);
        if (shopItem == null) return false;

        // Check currency
        if (!hasCurrency(player, shopItem.currencyType, shopItem.cost)) {
            player.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.prefix") +
                    "&cNon hai abbastanza &e" + shopItem.currencyName + "&c!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return false;
        }

        removeCurrency(player, shopItem.currencyType, shopItem.cost);
        giveItem(player, shopItem, game);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        return true;
    }

    private boolean hasCurrency(Player player, Material type, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == type) count += item.getAmount();
        }
        return count >= amount;
    }

    private void removeCurrency(Player player, Material type, int amount) {
        int toRemove = amount;
        for (int i = 0; i < player.getInventory().getSize() && toRemove > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == type) {
                if (item.getAmount() <= toRemove) {
                    toRemove -= item.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - toRemove);
                    toRemove = 0;
                }
            }
        }
    }

    private void giveItem(Player player, ShopItem shopItem, it.vertex.bedwars.game.BedwarsGame game) {
        ItemStack item = shopItem.result.clone();

        // Apply team sharpness upgrade
        if (game != null && shopItem.applySharpness) {
            BedwarsTeam team = game.getTeam(player);
            if (team != null) {
                int level = game.getTeamDataMap().get(team).getSharpnessLevel();
                if (level > 0) item.addUnsafeEnchantment(Enchantment.SHARPNESS, level);
            }
        }

        // Apply team protection upgrade
        if (game != null && shopItem.applyProtection && item.getType().name().contains("_BOOTS") ||
                (game != null && shopItem.applyProtection && item.getType().name().contains("_LEGGINGS"))) {
            BedwarsTeam team = game.getTeam(player);
            if (team != null) {
                int level = game.getTeamDataMap().get(team).getProtectionLevel();
                if (level > 0) item.addUnsafeEnchantment(Enchantment.PROTECTION, level);
            }
        }

        // Replace existing pickaxe/sword
        if (shopItem.replaceExisting) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack existing = player.getInventory().getItem(i);
                if (existing != null && isSameCategory(existing.getType(), item.getType())) {
                    player.getInventory().setItem(i, null);
                    break;
                }
            }
        }

        player.getInventory().addItem(item);
    }

    private boolean isSameCategory(Material a, Material b) {
        boolean aPickaxe = a.name().contains("_PICKAXE");
        boolean bPickaxe = b.name().contains("_PICKAXE");
        boolean aSword = a.name().contains("_SWORD");
        boolean bSword = b.name().contains("_SWORD");
        boolean aAxe = a.name().contains("_AXE");
        boolean bAxe = b.name().contains("_AXE");
        return (aPickaxe && bPickaxe) || (aSword && bSword) || (aAxe && bAxe);
    }

    public void giveColoredArmor(Player player, BedwarsTeam team) {
        Color color = getArmorColor(team);
        for (Material mat : new Material[]{Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE}) {
            ItemStack armor = new ItemStack(mat);
            LeatherArmorMeta lam = (LeatherArmorMeta) armor.getItemMeta();
            lam.setColor(color);
            armor.setItemMeta(lam);
            // Enchant so it won't be removed on death
            armor.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            player.getInventory().setItem(mat == Material.LEATHER_HELMET ? 39 : 38, armor);
        }
    }

    private Color getArmorColor(BedwarsTeam team) {
        return switch (team) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.GREEN;
            case YELLOW -> Color.YELLOW;
            case AQUA -> Color.AQUA;
            case WHITE -> Color.WHITE;
            case PINK -> Color.fromRGB(255, 105, 180);
            case GRAY -> Color.GRAY;
        };
    }

    private ItemStack createShopItem(String name, String lore, boolean filler) {
        return ItemBuilder.named(filler ? Material.CYAN_STAINED_GLASS_PANE : Material.AIR, name)
                .build();
    }

    // ── Shop Item Definitions ─────────────────────────────────────────────────

    record ShopItem(ItemStack result, Material currencyType, int cost, String currencyName,
                    boolean applySharpness, boolean applyProtection, boolean replaceExisting) {}

    private ShopItem getShopItem(String name) {
        return switch (name) {
            case "Blocchi di legno" -> new ShopItem(new ItemStack(Material.OAK_PLANKS, 16), Material.IRON_INGOT, 2, "Ferro", false, false, false);
            case "Terracotta" -> new ShopItem(new ItemStack(Material.TERRACOTTA, 16), Material.IRON_INGOT, 4, "Ferro", false, false, false);
            case "Pietra del Nether" -> new ShopItem(new ItemStack(Material.END_STONE, 4), Material.IRON_INGOT, 2, "Ferro", false, false, false);
            case "Scale" -> new ShopItem(new ItemStack(Material.LADDER, 4), Material.IRON_INGOT, 2, "Ferro", false, false, false);
            case "Osservatorio" -> new ShopItem(new ItemStack(Material.OBSIDIAN, 4), Material.DIAMOND, 4, "Diamanti", false, false, false);
            case "Vetro esplosivo" -> new ShopItem(new ItemStack(Material.GLASS, 4), Material.IRON_INGOT, 2, "Ferro", false, false, false);
            case "Spada di pietra" -> new ShopItem(new ItemStack(Material.STONE_SWORD), Material.IRON_INGOT, 10, "Ferro", true, false, true);
            case "Spada di ferro" -> new ShopItem(new ItemStack(Material.IRON_SWORD), Material.GOLD_INGOT, 7, "Oro", true, false, true);
            case "Spada di diamante" -> new ShopItem(new ItemStack(Material.DIAMOND_SWORD), Material.DIAMOND, 3, "Diamanti", true, false, true);
            case "Arco" -> new ShopItem(new ItemStack(Material.BOW), Material.GOLD_INGOT, 3, "Oro", false, false, false);
            case "Frecce" -> new ShopItem(new ItemStack(Material.ARROW, 8), Material.GOLD_INGOT, 2, "Oro", false, false, false);
            case "Stivali di cotta" -> new ShopItem(new ItemStack(Material.CHAINMAIL_BOOTS), Material.IRON_INGOT, 40, "Ferro", false, true, true);
            case "Pantaloni di cotta" -> new ShopItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), Material.GOLD_INGOT, 3, "Oro", false, true, true);
            case "Stivali di ferro" -> new ShopItem(new ItemStack(Material.IRON_BOOTS), Material.GOLD_INGOT, 7, "Oro", false, true, true);
            case "Pantaloni di ferro" -> new ShopItem(new ItemStack(Material.IRON_LEGGINGS), Material.DIAMOND, 4, "Diamanti", false, true, true);
            case "Stivali di diamante" -> new ShopItem(new ItemStack(Material.DIAMOND_BOOTS), Material.DIAMOND, 6, "Diamanti", false, true, true);
            case "Pantaloni di diamante" -> new ShopItem(new ItemStack(Material.DIAMOND_LEGGINGS), Material.DIAMOND, 8, "Diamanti", false, true, true);
            case "Piccone di pietra" -> new ShopItem(new ItemStack(Material.STONE_PICKAXE), Material.IRON_INGOT, 10, "Ferro", false, false, true);
            case "Piccone di ferro" -> new ShopItem(new ItemStack(Material.IRON_PICKAXE), Material.GOLD_INGOT, 5, "Oro", false, false, true);
            case "Piccone di diamante" -> new ShopItem(new ItemStack(Material.DIAMOND_PICKAXE), Material.DIAMOND, 3, "Diamanti", false, false, true);
            case "Ascia di ferro" -> new ShopItem(new ItemStack(Material.IRON_AXE), Material.GOLD_INGOT, 3, "Oro", true, false, true);
            case "Forbici" -> new ShopItem(new ItemStack(Material.SHEARS), Material.IRON_INGOT, 20, "Ferro", false, false, false);
            case "Mela d'oro" -> new ShopItem(new ItemStack(Material.GOLDEN_APPLE), Material.GOLD_INGOT, 3, "Oro", false, false, false);
            case "Perla dell'Ender" -> new ShopItem(new ItemStack(Material.ENDER_PEARL), Material.DIAMOND, 4, "Diamanti", false, false, false);
            case "TNT" -> new ShopItem(new ItemStack(Material.TNT), Material.DIAMOND, 4, "Diamanti", false, false, false);
            case "Palla di fuoco" -> new ShopItem(new ItemStack(Material.FIRE_CHARGE), Material.IRON_INGOT, 40, "Ferro", false, false, false);
            case "Ragnatela" -> new ShopItem(new ItemStack(Material.COBWEB), Material.GOLD_INGOT, 4, "Oro", false, false, false);
            case "Acciarino" -> new ShopItem(new ItemStack(Material.FLINT_AND_STEEL), Material.GOLD_INGOT, 4, "Oro", false, false, false);
            default -> null;
        };
    }
}
