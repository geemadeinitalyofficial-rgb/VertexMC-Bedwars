package it.vertex.bedwars.generators;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.BedwarsGame;
import it.vertex.bedwars.game.BedwarsTeam;
import it.vertex.bedwars.game.TeamData;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class ResourceGenerator {

    public enum Type {
        IRON(Material.IRON_INGOT, 2, 32, "&fFerro", ChatColor.WHITE),
        GOLD(Material.GOLD_INGOT, 8, 16, "&6Oro", ChatColor.GOLD),
        DIAMOND(Material.DIAMOND, 30, 4, "&bDiamante", ChatColor.AQUA),
        EMERALD(Material.EMERALD, 60, 2, "&aSmeraldo", ChatColor.GREEN);

        public final Material material;
        public final int defaultDelay; // ticks per item
        public final int maxItems;
        public final String displayName;
        public final ChatColor color;

        Type(Material material, int defaultDelay, int maxItems, String displayName, ChatColor color) {
            this.material = material;
            this.defaultDelay = defaultDelay;
            this.maxItems = maxItems;
            this.displayName = displayName;
            this.color = color;
        }
    }

    private final VertexBedwars plugin;
    private final BedwarsGame game;
    private final Location location;
    private final Type type;
    private final BedwarsTeam team; // null for shared generators
    private int tickCounter = 0;
    private int currentDelay;
    private ArmorStand hologram;
    private ArmorStand timerHologram;

    public ResourceGenerator(VertexBedwars plugin, BedwarsGame game, Location location, Type type, BedwarsTeam team) {
        this.plugin = plugin;
        this.game = game;
        this.location = location.clone().add(0.5, 0, 0.5);
        this.type = type;
        this.team = team;
        this.currentDelay = type.defaultDelay;
    }

    public void start() {
        spawnHolograms();
    }

    public void tick() {
        // Apply forge speed bonus for team generators
        int delay = currentDelay;
        if (team != null) {
            TeamData data = game.getTeamDataMap().get(team);
            if (data != null) {
                int forgeLevel = data.getForgeLevel();
                if (forgeLevel == 1) delay = (int)(currentDelay * 0.75);
                else if (forgeLevel == 2) delay = (int)(currentDelay * 0.5);
                else if (forgeLevel == 3) delay = (int)(currentDelay * 0.25);
                else if (forgeLevel >= 4) delay = 1;
            }
        }

        tickCounter++;
        if (tickCounter >= delay) {
            tickCounter = 0;
            spawnItem();
        }

        updateTimerHologram(delay);
    }

    private void spawnItem() {
        // Count existing items
        long count = location.getWorld().getNearbyEntities(location, 2, 1, 2).stream()
                .filter(e -> e instanceof Item)
                .filter(e -> ((Item) e).getItemStack().getType() == type.material)
                .count();

        if (count >= type.maxItems) return;

        Item item = location.getWorld().dropItem(location.clone().add(0, 0.2, 0), new ItemStack(type.material));
        item.setPickupDelay(0);
        item.setVelocity(item.getVelocity().multiply(0));

        // Spinning effect
        location.getWorld().spawnParticle(Particle.ITEM_SLIME, location.clone().add(0, 0.5, 0), 3, 0.2, 0.2, 0.2, 0);
    }

    private void spawnHolograms() {
        // Name hologram
        hologram = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.8, 0), EntityType.ARMOR_STAND);
        hologram.setCustomName(MessageUtil.color(type.displayName));
        hologram.setCustomNameVisible(true);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setInvulnerable(true);
        hologram.setSmall(true);

        // Timer hologram
        timerHologram = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.4, 0), EntityType.ARMOR_STAND);
        timerHologram.setCustomName(MessageUtil.color("&eTier I"));
        timerHologram.setCustomNameVisible(true);
        timerHologram.setVisible(false);
        timerHologram.setGravity(false);
        timerHologram.setInvulnerable(true);
        timerHologram.setSmall(true);
    }

    private void updateTimerHologram(int delay) {
        if (timerHologram == null || !timerHologram.isValid()) return;
        int tier = getTierForType();
        String tierName = tier == 1 ? "&eTier I" : tier == 2 ? "&bTier II" : "&6Tier III";
        timerHologram.setCustomName(MessageUtil.color(tierName));
    }

    private int getTierForType() {
        if (type == Type.DIAMOND) return game.getDiamondTier();
        if (type == Type.EMERALD) return game.getEmeraldTier();
        return 1;
    }

    public void stop() {
        if (hologram != null && hologram.isValid()) hologram.remove();
        if (timerHologram != null && timerHologram.isValid()) timerHologram.remove();
    }

    public void setDelay(int delay) { this.currentDelay = delay; }
    public Type getType() { return type; }
    public BedwarsTeam getTeam() { return team; }
    public Location getLocation() { return location; }
}
