package it.vertex.bedwars.utils;

import it.vertex.bedwars.game.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class DeathAnimationUtil {

    /**
     * Mostra effetti visivi e sonori quando un giocatore muore.
     */
    public static void playDeathEffect(Player victim, Player killer, BedwarsTeam victimTeam, boolean definitive) {
        Location loc = victim.getLocation();

        // Particelle al punto di morte
        loc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc.clone().add(0, 1, 0), 30, 0.4, 0.4, 0.4, 0.1);
        loc.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc.clone().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0);
        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_DEATH, 1f, 1f);

        if (definitive) {
            // Effetto più drammatico per eliminazione definitiva
            loc.getWorld().spawnParticle(Particle.EXPLOSION, loc.clone().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0);
            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_HURT, 0.5f, 1.2f);
        }

        // Titolo per l'eliminato
        if (definitive) {
            victim.sendTitle(
                    MessageUtil.color("&c&lELIMINATO!"),
                    killer != null ? MessageUtil.color("&7Eliminato da &f" + killer.getName()) :
                            MessageUtil.color("&7Sei stato eliminato!"),
                    10, 70, 20
            );
        } else {
            victim.sendTitle(
                    MessageUtil.color("&c&lSEI MORTO!"),
                    MessageUtil.color("&eRiapparirai tra poco..."),
                    10, 50, 20
            );
        }

        // Titolo per il killer
        if (killer != null && killer != victim) {
            killer.sendTitle(
                    MessageUtil.color("&a&l✔ UCCISO!"),
                    MessageUtil.color("&f" + victim.getName()),
                    5, 30, 10
            );
            killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
        }
    }

    /**
     * Effetto visivo quando un letto viene distrutto.
     */
    public static void playBedDestroyEffect(Location bedLocation, BedwarsTeam team) {
        if (bedLocation == null || bedLocation.getWorld() == null) return;
        bedLocation.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, bedLocation.clone().add(0, 0.5, 0), 3);
        bedLocation.getWorld().spawnParticle(Particle.SMOKE, bedLocation.clone().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);
        bedLocation.getWorld().playSound(bedLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.5f);
        bedLocation.getWorld().playSound(bedLocation, Sound.BLOCK_GLASS_BREAK, 1f, 0.8f);
    }
}
