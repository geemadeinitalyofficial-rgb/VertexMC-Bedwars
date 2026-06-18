package it.vertex.bedwars.game;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class Arena {

    private final String name;
    private String displayName;
    private int maxTeams;
    private int playersPerTeam;
    private Location lobbySpawn;
    private Location spectatorSpawn;
    private final Map<BedwarsTeam, Location> teamSpawns = new HashMap<>();
    private final Map<BedwarsTeam, Location> bedLocations = new HashMap<>();
    private final List<Location> diamondGenerators = new ArrayList<>();
    private final List<Location> emeraldGenerators = new ArrayList<>();
    private final List<Location> shopVillagers = new ArrayList<>();
    private final List<Location> upgradeVillagers = new ArrayList<>();
    private boolean configured = false;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.maxTeams = 4;
        this.playersPerTeam = 2;
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String n) { this.displayName = n; }
    public int getMaxTeams() { return maxTeams; }
    public void setMaxTeams(int t) { this.maxTeams = t; }
    public int getPlayersPerTeam() { return playersPerTeam; }
    public void setPlayersPerTeam(int p) { this.playersPerTeam = p; }
    public int getMaxPlayers() { return maxTeams * playersPerTeam; }
    public Location getLobbySpawn() { return lobbySpawn; }
    public void setLobbySpawn(Location l) { this.lobbySpawn = l; }
    public Location getSpectatorSpawn() { return spectatorSpawn; }
    public void setSpectatorSpawn(Location l) { this.spectatorSpawn = l; }
    public Map<BedwarsTeam, Location> getTeamSpawns() { return teamSpawns; }
    public Map<BedwarsTeam, Location> getBedLocations() { return bedLocations; }
    public List<Location> getDiamondGenerators() { return diamondGenerators; }
    public List<Location> getEmeraldGenerators() { return emeraldGenerators; }
    public List<Location> getShopVillagers() { return shopVillagers; }
    public List<Location> getUpgradeVillagers() { return upgradeVillagers; }
    public boolean isConfigured() { return configured; }
    public void setConfigured(boolean c) { this.configured = c; }

    public List<BedwarsTeam> getEnabledTeams() {
        List<BedwarsTeam> teams = new ArrayList<>();
        BedwarsTeam[] all = BedwarsTeam.values();
        for (int i = 0; i < maxTeams && i < all.length; i++) {
            teams.add(all[i]);
        }
        return teams;
    }

    public void save(File file) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", name);
        config.set("display-name", displayName);
        config.set("max-teams", maxTeams);
        config.set("players-per-team", playersPerTeam);
        config.set("configured", configured);
        if (lobbySpawn != null) config.set("lobby-spawn", serializeLocation(lobbySpawn));
        if (spectatorSpawn != null) config.set("spectator-spawn", serializeLocation(spectatorSpawn));
        teamSpawns.forEach((team, loc) -> config.set("spawns." + team.name(), serializeLocation(loc)));
        bedLocations.forEach((team, loc) -> config.set("beds." + team.name(), serializeLocation(loc)));
        for (int i = 0; i < diamondGenerators.size(); i++)
            config.set("generators.diamond." + i, serializeLocation(diamondGenerators.get(i)));
        for (int i = 0; i < emeraldGenerators.size(); i++)
            config.set("generators.emerald." + i, serializeLocation(emeraldGenerators.get(i)));
        for (int i = 0; i < shopVillagers.size(); i++)
            config.set("villagers.shop." + i, serializeLocation(shopVillagers.get(i)));
        for (int i = 0; i < upgradeVillagers.size(); i++)
            config.set("villagers.upgrade." + i, serializeLocation(upgradeVillagers.get(i)));
        try { config.save(file); } catch (Exception e) { e.printStackTrace(); }
    }

    public static Arena load(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String arenaName = config.getString("name", file.getName().replace(".yml", ""));
        Arena arena = new Arena(arenaName);
        arena.displayName = config.getString("display-name", arenaName);
        arena.maxTeams = config.getInt("max-teams", 4);
        arena.playersPerTeam = config.getInt("players-per-team", 2);
        arena.configured = config.getBoolean("configured", false);
        if (config.contains("lobby-spawn"))
            arena.lobbySpawn = deserializeLocation(config.getConfigurationSection("lobby-spawn"));
        if (config.contains("spectator-spawn"))
            arena.spectatorSpawn = deserializeLocation(config.getConfigurationSection("spectator-spawn"));
        if (config.contains("spawns"))
            for (String key : config.getConfigurationSection("spawns").getKeys(false))
                try { arena.teamSpawns.put(BedwarsTeam.valueOf(key),
                        deserializeLocation(config.getConfigurationSection("spawns." + key))); } catch (Exception ignored) {}
        if (config.contains("beds"))
            for (String key : config.getConfigurationSection("beds").getKeys(false))
                try { arena.bedLocations.put(BedwarsTeam.valueOf(key),
                        deserializeLocation(config.getConfigurationSection("beds." + key))); } catch (Exception ignored) {}
        if (config.contains("generators.diamond"))
            for (String key : config.getConfigurationSection("generators.diamond").getKeys(false))
                arena.diamondGenerators.add(deserializeLocation(config.getConfigurationSection("generators.diamond." + key)));
        if (config.contains("generators.emerald"))
            for (String key : config.getConfigurationSection("generators.emerald").getKeys(false))
                arena.emeraldGenerators.add(deserializeLocation(config.getConfigurationSection("generators.emerald." + key)));
        if (config.contains("villagers.shop"))
            for (String key : config.getConfigurationSection("villagers.shop").getKeys(false))
                arena.shopVillagers.add(deserializeLocation(config.getConfigurationSection("villagers.shop." + key)));
        if (config.contains("villagers.upgrade"))
            for (String key : config.getConfigurationSection("villagers.upgrade").getKeys(false))
                arena.upgradeVillagers.add(deserializeLocation(config.getConfigurationSection("villagers.upgrade." + key)));
        return arena;
    }

    private static Map<String, Object> serializeLocation(Location loc) {
        Map<String, Object> map = new HashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX()); map.put("y", loc.getY()); map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw()); map.put("pitch", loc.getPitch());
        return map;
    }

    private static Location deserializeLocation(ConfigurationSection section) {
        if (section == null) return null;
        return new Location(
                org.bukkit.Bukkit.getWorld(section.getString("world", "world")),
                section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
    }
}
