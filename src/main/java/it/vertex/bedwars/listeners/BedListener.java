package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;

public class BedListener implements Listener {

    private final VertexBedwars plugin;

    public BedListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BedwarsGame game = plugin.getGameManager().getGame(player);

        if (game == null || game.getState() != GameState.IN_GAME) return;

        // Check if it's a bed
        if (!block.getType().name().endsWith("_BED")) {
            // Check if it's a placed block
            if (!game.getPlacedBlocks().contains(block.getLocation())) {
                event.setCancelled(true);
            }
            return;
        }

        // Find which team's bed this is
        BedwarsTeam bedOwner = null;
        for (BedwarsTeam team : game.getArena().getEnabledTeams()) {
            Location bedLoc = game.getArena().getBedLocations().get(team);
            if (bedLoc != null && isSameBed(bedLoc, block.getLocation())) {
                bedOwner = team;
                break;
            }
        }

        if (bedOwner == null) return;

        // Can't break your own bed
        BedwarsTeam playerTeam = game.getTeam(player);
        if (playerTeam == bedOwner) {
            event.setCancelled(true);
            player.sendMessage("\u00A7cNon puoi rompere il tuo stesso letto!");
            return;
        }

        // Bed already destroyed
        if (!game.getTeamDataMap().get(bedOwner).hasBed()) {
            event.setCancelled(true);
            return;
        }

        // Break the bed
        event.setDropItems(false);
        game.handleBedDestroyed(bedOwner, player);
    }

    private boolean isSameBed(Location a, Location b) {
        // Beds are 2 blocks - check both halves
        return (Math.abs(a.getBlockX() - b.getBlockX()) <= 1 &&
                a.getBlockY() == b.getBlockY() &&
                Math.abs(a.getBlockZ() - b.getBlockZ()) <= 1 &&
                a.getWorld().equals(b.getWorld()));
    }
}
