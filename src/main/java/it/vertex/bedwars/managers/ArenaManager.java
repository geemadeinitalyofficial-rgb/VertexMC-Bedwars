package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.Arena;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ArenaManager {

    private final VertexBedwars plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final File arenasFolder;

    public ArenaManager(VertexBedwars plugin) {
        this.plugin = plugin;
        this.arenasFolder = new File(plugin.getDataFolder(), "arenas");
        if (!arenasFolder.exists()) arenasFolder.mkdirs();
    }

    public void loadArenas() {
        arenas.clear();
        File[] files = arenasFolder.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            try {
                Arena arena = Arena.load(file);
                arenas.put(arena.getName().toLowerCase(), arena);
                plugin.getLogger().info("Caricata arena: " + arena.getName());
            } catch (Exception e) {
                plugin.getLogger().warning("Errore caricando arena: " + file.getName());
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Caricate " + arenas.size() + " arene.");
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public Arena createArena(String name) {
        Arena arena = new Arena(name);
        arenas.put(name.toLowerCase(), arena);
        saveArena(arena);
        return arena;
    }

    public void saveArena(Arena arena) {
        File file = new File(arenasFolder, arena.getName() + ".yml");
        arena.save(file);
    }

    public void deleteArena(String name) {
        arenas.remove(name.toLowerCase());
        File file = new File(arenasFolder, name + ".yml");
        file.delete();
    }

    public boolean arenaExists(String name) {
        return arenas.containsKey(name.toLowerCase());
    }
}
