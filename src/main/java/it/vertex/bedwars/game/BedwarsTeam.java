package it.vertex.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum BedwarsTeam {
    RED(ChatColor.RED, "Rosso", Material.RED_BED, Material.RED_WOOL, Material.RED_STAINED_GLASS),
    BLUE(ChatColor.BLUE, "Blu", Material.BLUE_BED, Material.BLUE_WOOL, Material.BLUE_STAINED_GLASS),
    GREEN(ChatColor.GREEN, "Verde", Material.GREEN_BED, Material.GREEN_WOOL, Material.GREEN_STAINED_GLASS),
    YELLOW(ChatColor.YELLOW, "Giallo", Material.YELLOW_BED, Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS),
    AQUA(ChatColor.AQUA, "Acqua", Material.CYAN_BED, Material.CYAN_WOOL, Material.CYAN_STAINED_GLASS),
    WHITE(ChatColor.WHITE, "Bianco", Material.WHITE_BED, Material.WHITE_WOOL, Material.WHITE_STAINED_GLASS),
    PINK(ChatColor.LIGHT_PURPLE, "Rosa", Material.PINK_BED, Material.PINK_WOOL, Material.PINK_STAINED_GLASS),
    GRAY(ChatColor.GRAY, "Grigio", Material.GRAY_BED, Material.GRAY_WOOL, Material.GRAY_STAINED_GLASS);

    private final ChatColor color;
    private final String displayName;
    private final Material bedMaterial;
    private final Material woolMaterial;
    private final Material glassMaterial;

    BedwarsTeam(ChatColor color, String displayName, Material bedMaterial, Material woolMaterial, Material glassMaterial) {
        this.color = color;
        this.displayName = displayName;
        this.bedMaterial = bedMaterial;
        this.woolMaterial = woolMaterial;
        this.glassMaterial = glassMaterial;
    }

    public ChatColor getColor() { return color; }
    public String getDisplayName() { return displayName; }
    public String getColoredName() { return color + displayName; }
    public Material getBedMaterial() { return bedMaterial; }
    public Material getWoolMaterial() { return woolMaterial; }
    public Material getGlassMaterial() { return glassMaterial; }

    public String getPrefix() {
        return color + "⬛ " + ChatColor.RESET;
    }
}
