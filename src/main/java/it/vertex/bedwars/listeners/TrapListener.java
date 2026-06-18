package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.managers.TrapManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class TrapListener implements Listener {

    private final VertexBedwars plugin;
    // Cooldown per non triggerare la stessa trappola 2 volte in rapida successione
    private final Set<UUID> recentlyTriggered = new HashSet<>();

    public TrapListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Controlla solo se ha cambiato blocco
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null || game.getState() != GameState.IN_GAME) return;
        if (recentlyTriggered.contains(player.getUniqueId())) return;

        BedwarsTeam playerTeam = game.getTeam(player);
        if (playerTeam == null) return;

        // Controlla se il giocatore è vicino alla base di un team nemico
        for (Map.Entry<BedwarsTeam, TeamData> entry : game.getTeamDataMap().entrySet()) {
            BedwarsTeam defenderTeam = entry.getKey();
            if (defenderTeam == playerTeam) continue; // Non triggerare la propria trappola

            TeamData data = entry.getValue();
            if (data.isEliminated()) continue;

            org.bukkit.Location spawnLoc = data.getSpawnLocation();
            if (spawnLoc == null) continue;

            // Raggio base = 8 blocchi dal spawn del team
            if (player.getLocation().getWorld().equals(spawnLoc.getWorld()) &&
                player.getLocation().distance(spawnLoc) <= 8) {

                // Controlla se il team difensore ha trappole
                if (!plugin.getTrapManager().getTraps(defenderTeam).isEmpty()) {
                    plugin.getTrapManager().checkAndTrigger(game, player, defenderTeam);

                    // Cooldown 3 secondi
                    recentlyTriggered.add(player.getUniqueId());
                    org.bukkit.Bukkit.getScheduler().runTaskLater(plugin,
                            () -> recentlyTriggered.remove(player.getUniqueId()), 60L);
                }
            }
        }
    }
}
