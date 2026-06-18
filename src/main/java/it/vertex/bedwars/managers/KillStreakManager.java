package it.vertex.bedwars.managers;

import it.vertex.bedwars.game.BedwarsGame;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class KillStreakManager {

    private final Map<UUID, Integer> streaks = new HashMap<>();

    public void onKill(Player killer, BedwarsGame game) {
        int streak = streaks.merge(killer.getUniqueId(), 1, Integer::sum);
        String msg = getStreakMessage(streak);
        if (msg != null) {
            game.broadcast(MessageUtil.color("&6[SERIE] &f" + killer.getName() + msg));
            killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f + (streak * 0.1f));
        }
    }

    public void onDeath(Player victim) {
        int streak = streaks.getOrDefault(victim.getUniqueId(), 0);
        streaks.put(victim.getUniqueId(), 0);
        if (streak >= 3) {
            // Il killer che ha fermato la serie riceve un messaggio extra (gestito altrove)
        }
    }

    public int getStreak(Player player) {
        return streaks.getOrDefault(player.getUniqueId(), 0);
    }

    public void reset(Player player) {
        streaks.put(player.getUniqueId(), 0);
    }

    public void resetAll() {
        streaks.clear();
    }

    private String getStreakMessage(int streak) {
        return switch (streak) {
            case 3  -> " &eè in serie di &63 kill &e! &7(Tripletta)";
            case 5  -> " &eè in serie di &a5 kill &e! &7(Imbattibile)";
            case 7  -> " &eè in serie di &b7 kill &e! &7(Dominante)";
            case 10 -> " &eè in serie di &c10 kill &e! &7(LEGGENDARIO!)";
            default -> streak > 10 && streak % 5 == 0 ?
                    " &eha raggiunto &c" + streak + " kill &econsecutive! &7(DIVINO!)" : null;
        };
    }
}
