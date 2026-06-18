package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private final VertexBedwars plugin;
    private final Map<String, BedwarsGame> activeGames = new HashMap<>();
    private final Map<UUID, BedwarsGame> playerGameMap = new HashMap<>();

    public GameManager(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    public boolean joinGame(Player player, String arenaName) {
        if (playerGameMap.containsKey(player.getUniqueId())) {
            player.sendMessage(msg("&cSei già in una partita!"));
            return false;
        }
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage(msg("&cArena non trovata!"));
            return false;
        }
        if (!arena.isConfigured()) {
            player.sendMessage(msg("&cL'arena non è ancora configurata!"));
            return false;
        }

        BedwarsGame game = activeGames.computeIfAbsent(arenaName.toLowerCase(),
                k -> new BedwarsGame(plugin, arena));

        if (game.getState() == GameState.IN_GAME || game.getState() == GameState.ENDING) {
            player.sendMessage(msg("&cLa partita è già in corso! Puoi fare lo spettatore."));
            return false;
        }

        if (!game.addPlayer(player)) {
            player.sendMessage(msg("&cLa partita è piena!"));
            return false;
        }

        playerGameMap.put(player.getUniqueId(), game);
        return true;
    }

    public void leaveGame(Player player) {
        BedwarsGame game = playerGameMap.remove(player.getUniqueId());
        if (game == null) {
            player.sendMessage(msg("&cNon sei in una partita!"));
            return;
        }
        game.removePlayer(player, true);
    }

    public BedwarsGame getGame(Player player) {
        return playerGameMap.get(player.getUniqueId());
    }

    public BedwarsGame getGame(String arenaName) {
        return activeGames.get(arenaName.toLowerCase());
    }

    public boolean isInGame(Player player) {
        return playerGameMap.containsKey(player.getUniqueId());
    }

    public void onGameReset(BedwarsGame game) {
        // Remove all player mappings for this game
        playerGameMap.entrySet().removeIf(e -> e.getValue() == game);
        activeGames.remove(game.getArena().getName().toLowerCase());
    }

    public void stopAllGames() {
        for (BedwarsGame game : new ArrayList<>(activeGames.values())) {
            try { game.reset(); } catch (Exception ignored) {}
        }
        activeGames.clear();
        playerGameMap.clear();
    }

    public Collection<BedwarsGame> getActiveGames() {
        return activeGames.values();
    }

    private String msg(String s) {
        return plugin.getConfig().getString("messages.prefix", "&b&lVertex &f&lBedwars &8» &r") + s
                .replace("&", "\u00A7");
    }
}
