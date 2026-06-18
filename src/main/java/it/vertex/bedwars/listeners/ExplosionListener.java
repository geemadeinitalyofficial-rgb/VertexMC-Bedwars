package it.vertex.bedwars.listeners;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

public class ExplosionListener implements Listener {

    private final VertexBedwars plugin;

    public ExplosionListener(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    /** Impedisce la TNT di distruggere i letti e blocchi non piazzati */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        BedwarsGame game = getGameForLocation(event.getLocation());
        if (game == null) return;

        Iterator<Block> iter = event.blockList().iterator();
        while (iter.hasNext()) {
            Block block = iter.next();
            // Proteggi i letti
            if (block.getType().name().endsWith("_BED")) {
                iter.remove();
                continue;
            }
            // Rimuovi solo i blocchi piazzati dai giocatori
            if (!game.getPlacedBlocks().contains(block.getLocation())) {
                iter.remove();
            } else {
                game.getPlacedBlocks().remove(block.getLocation());
            }
        }
    }

    /** Palla di fuoco sparata con click destro */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.FIRE_CHARGE) return;
        Player player = event.getPlayer();
        BedwarsGame game = plugin.getGameManager().getGame(player);
        if (game == null || game.getState() != GameState.IN_GAME) return;

        event.setCancelled(true);

        // Consuma 1 fire charge
        ItemStack item = event.getItem();
        if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
        else player.getInventory().remove(item);

        // Lancia palla di fuoco
        Location eye = player.getEyeLocation();
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setDirection(eye.getDirection().multiply(1.5));
        fireball.setIsIncendiary(true);
        fireball.setYield(1.5f);
        fireball.setShooter(player);
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
    }

    /** Limita danno da esplosione palla di fuoco ai soli blocchi piazzati */
    @EventHandler
    public void onFireballExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Fireball)) return;
        BedwarsGame game = getGameForLocation(event.getLocation());
        if (game == null) return;

        event.blockList().removeIf(b ->
                b.getType().name().endsWith("_BED") ||
                !game.getPlacedBlocks().contains(b.getLocation()));
    }

    /** Impedisce l'incendio di blocchi non piazzati */
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        BedwarsGame game = getGameForLocation(event.getBlock().getLocation());
        if (game == null) return;
        if (!game.getPlacedBlocks().contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    /** Cancella danno esplosione su armature permanenti (elmo/petto in pelle colorata) */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        BedwarsGame game = plugin.getGameManager().getGame(victim);
        if (game == null) return;

        // TNT lancia il giocatore in aria (effetto vanilla) — ok, lascia passare
    }

    private BedwarsGame getGameForLocation(Location location) {
        for (BedwarsGame game : plugin.getGameManager().getActiveGames()) {
            if (game.getState() == GameState.IN_GAME) {
                // Semplice: se la location è nel mondo dell'arena
                Arena arena = game.getArena();
                Location spawn = arena.getTeamSpawns().values().stream().findFirst().orElse(null);
                if (spawn != null && spawn.getWorld().equals(location.getWorld())) {
                    return game;
                }
            }
        }
        return null;
    }
}
