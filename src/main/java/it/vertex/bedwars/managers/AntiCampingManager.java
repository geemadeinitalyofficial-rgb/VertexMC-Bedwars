package it.vertex.bedwars.managers;

import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

import java.util.Map;
import java.util.UUID;

/**
 * Simula il "Sudden Death" / Deathmatch stile CoralMC:
 * dopo 20 minuti i letti rimasti vengono automaticamente distrutti
 * e tutti i giocatori ricevono velocità + forza aumentata.
 */
public class AntiCampingManager {

    public static final int SUDDEN_DEATH_TIME = 20 * 60; // 20 minuti

    private boolean suddenDeathTriggered = false;

    public void tick(BedwarsGame game) {
        if (suddenDeathTriggered) return;
        if (game.getGameTimeSeconds() < SUDDEN_DEATH_TIME) return;

        triggerSuddenDeath(game);
    }

    private void triggerSuddenDeath(BedwarsGame game) {
        suddenDeathTriggered = true;

        game.broadcast(MessageUtil.color("&c&l⚠ SUDDEN DEATH! ⚠"));
        game.broadcast(MessageUtil.color("&eTutti i letti rimasti sono stati distrutti!"));
        game.broadcast(MessageUtil.color("&6L'ultimo team in piedi vince!"));

        // Distruggi tutti i letti rimasti
        for (Map.Entry<BedwarsTeam, TeamData> entry : game.getTeamDataMap().entrySet()) {
            if (entry.getValue().hasBed()) {
                game.handleBedDestroyed(entry.getKey(), null);
            }
        }

        // Applica effetti a tutti i giocatori
        for (Player player : game.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0, true, true));
            player.sendTitle(
                    MessageUtil.color("&c&lSUDDEN DEATH!"),
                    MessageUtil.color("&eElimina tutti i nemici!"),
                    10, 80, 20
            );
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
        }
    }

    public boolean isSuddenDeath() { return suddenDeathTriggered; }

    public void reset() { suddenDeathTriggered = false; }
}
