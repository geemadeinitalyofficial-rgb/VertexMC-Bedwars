package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class StatsManager {

    private final VertexBedwars plugin;
    private final File statsFile;
    private final YamlConfiguration stats;

    public StatsManager(VertexBedwars plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), "stats.yml");
        this.stats = statsFile.exists() ?
                YamlConfiguration.loadConfiguration(statsFile) : new YamlConfiguration();
    }

    public int getKills(Player player) { return stats.getInt(player.getUniqueId() + ".kills", 0); }
    public int getDeaths(Player player) { return stats.getInt(player.getUniqueId() + ".deaths", 0); }
    public int getWins(Player player) { return stats.getInt(player.getUniqueId() + ".wins", 0); }
    public int getLosses(Player player) { return stats.getInt(player.getUniqueId() + ".losses", 0); }
    public int getBedsDestroyed(Player player) { return stats.getInt(player.getUniqueId() + ".beds", 0); }
    public int getGamesPlayed(Player player) { return stats.getInt(player.getUniqueId() + ".games", 0); }

    public void addKill(Player player) {
        stats.set(player.getUniqueId() + ".kills", getKills(player) + 1);
        save();
    }

    public void addDeath(Player player) {
        stats.set(player.getUniqueId() + ".deaths", getDeaths(player) + 1);
        save();
    }

    public void addWin(Player player) {
        stats.set(player.getUniqueId() + ".wins", getWins(player) + 1);
        stats.set(player.getUniqueId() + ".games", getGamesPlayed(player) + 1);
        save();
    }

    public void addLoss(Player player) {
        stats.set(player.getUniqueId() + ".losses", getLosses(player) + 1);
        stats.set(player.getUniqueId() + ".games", getGamesPlayed(player) + 1);
        save();
    }

    public void addBedDestroyed(Player player) {
        if (player == null) return;
        stats.set(player.getUniqueId() + ".beds", getBedsDestroyed(player) + 1);
        save();
    }

    public double getKDR(Player player) {
        int deaths = getDeaths(player);
        return deaths == 0 ? getKills(player) : (double) getKills(player) / deaths;
    }

    private void save() {
        try { stats.save(statsFile); } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveAll() { save(); }
}
