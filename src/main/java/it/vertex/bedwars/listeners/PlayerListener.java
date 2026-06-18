package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.Player;

public class PlayerListener implements Listener {

    private final VertexBedwars plugin;

    public PlayerListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().isInGame(player)) {
            plugin.getGameManager().leaveGame(player);
        }
        plugin.getScoreboardManager().removePlayer(player);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;
        // Allow dropping items in game (strategic drops)
    }
}
