package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

public class BlockListener implements Listener {

    private final VertexBedwars plugin;

    public BlockListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null || game.getState() != GameState.IN_GAME) return;
        // Track block for cleanup
        game.getPlacedBlocks().add(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null || game.getState() != GameState.IN_GAME) return;

        // Only allow breaking placed blocks (bed handled in BedListener)
        if (!game.getPlacedBlocks().contains(event.getBlock().getLocation())) {
            if (!event.getBlock().getType().name().endsWith("_BED")) {
                event.setCancelled(true);
            }
        } else {
            game.getPlacedBlocks().remove(event.getBlock().getLocation());
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        BedwarsGame game = findNearbyGame(event.getBlock().getLocation());
        if (game != null) event.setCancelled(true);
    }

    private BedwarsGame findNearbyGame(org.bukkit.Location location) {
        for (BedwarsGame game : plugin.getGameManager().getActiveGames()) {
            if (game.getState() == GameState.IN_GAME) return game;
        }
        return null;
    }
}
