package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

import java.util.*;

public class TrapManager {

    public enum TrapType {
        FIRST_AID("Pronto Soccorso", "&c❤ Trappola Pronto Soccorso"),
        MINER_FATIGUE("Disturbo Minatore", "&8⛏ Trappola Disturbo Minatore"),
        ALARM("Allarme", "&e🔔 Trappola Allarme");

        public final String displayName;
        public final String broadcastMsg;

        TrapType(String displayName, String broadcastMsg) {
            this.displayName = displayName;
            this.broadcastMsg = broadcastMsg;
        }
    }

    private final VertexBedwars plugin;
    // team -> lista trappole in coda
    private final Map<BedwarsTeam, Queue<TrapType>> trapQueues = new HashMap<>();

    public TrapManager(VertexBedwars plugin) {
        this.plugin = plugin;
        for (BedwarsTeam team : BedwarsTeam.values()) {
            trapQueues.put(team, new LinkedList<>());
        }
    }

    public boolean addTrap(BedwarsTeam team, TrapType type) {
        Queue<TrapType> queue = trapQueues.get(team);
        if (queue.size() >= 3) return false; // massimo 3 trappole in coda
        queue.add(type);
        return true;
    }

    /**
     * Chiamato quando un nemico entra nel raggio della base del team.
     */
    public void checkAndTrigger(BedwarsGame game, Player intruder, BedwarsTeam defenderTeam) {
        Queue<TrapType> queue = trapQueues.get(defenderTeam);
        if (queue.isEmpty()) return;

        TrapType trap = queue.poll();
        triggerTrap(game, intruder, defenderTeam, trap);
    }

    private void triggerTrap(BedwarsGame game, Player intruder, BedwarsTeam defenderTeam, TrapType trap) {
        String intruderTeamColor = "";
        BedwarsTeam intruderTeam = game.getTeam(intruder);
        if (intruderTeam != null) intruderTeamColor = intruderTeam.getColor() + "";

        switch (trap) {
            case FIRST_AID -> {
                // Rimuove tutti gli effetti negativi dal team difensore
                for (UUID uid : game.getTeamDataMap().get(defenderTeam).getMembers()) {
                    Player ally = org.bukkit.Bukkit.getPlayer(uid);
                    if (ally != null) {
                        ally.removePotionEffect(PotionEffectType.BLINDNESS);
                        ally.removePotionEffect(PotionEffectType.SLOWNESS);
                        ally.removePotionEffect(PotionEffectType.WEAKNESS);
                        ally.removePotionEffect(PotionEffectType.POISON);
                        ally.removePotionEffect(PotionEffectType.NAUSEA);
                        ally.sendMessage(MessageUtil.color("&a✔ Trappola Pronto Soccorso attivata!"));
                    }
                }
                // Infligge cecità e rallentamento all'intruso
                intruder.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                intruder.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
            }
            case MINER_FATIGUE -> {
                intruder.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 2));
                for (UUID uid : game.getTeamDataMap().get(defenderTeam).getMembers()) {
                    Player ally = org.bukkit.Bukkit.getPlayer(uid);
                    if (ally != null)
                        ally.sendMessage(MessageUtil.color("&8⛏ Trappola Disturbo Minatore attivata!"));
                }
            }
            case ALARM -> {
                for (UUID uid : game.getTeamDataMap().get(defenderTeam).getMembers()) {
                    Player ally = org.bukkit.Bukkit.getPlayer(uid);
                    if (ally != null) {
                        ally.sendMessage(MessageUtil.color(
                                "&e⚠ &r" + intruderTeamColor + intruder.getName() +
                                " &eha attivato l'allarme nella vostra base!"));
                        ally.playSound(ally.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    }
                }
                return; // Nessun effetto sull'intruso
            }
        }

        // Notifica team
        String msg = MessageUtil.color(defenderTeam.getColoredName() + " &7» " +
                trap.broadcastMsg + " &7attivata contro " + intruderTeamColor + intruder.getName() + "&7!");
        game.broadcast(msg);
        intruder.playSound(intruder.getLocation(), Sound.ENTITY_WITHER_HURT, 1f, 1.5f);
    }

    public Queue<TrapType> getTraps(BedwarsTeam team) {
        return trapQueues.get(team);
    }

    public void reset() {
        for (BedwarsTeam team : BedwarsTeam.values()) {
            trapQueues.put(team, new LinkedList<>());
        }
    }
}
