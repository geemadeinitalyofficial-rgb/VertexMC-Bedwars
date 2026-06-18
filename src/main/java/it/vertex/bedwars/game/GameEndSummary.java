package it.vertex.bedwars.game;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameEndSummary {

    private final VertexBedwars plugin;

    public GameEndSummary(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    public void sendSummary(BedwarsGame game, BedwarsTeam winner) {
        String separator = MessageUtil.color("&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String empty = "";

        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(game.getOnlinePlayers());
        // Include spectators
        for (UUID uid : game.getPlayerTeams().keySet()) {
            Player p = Bukkit.getPlayer(uid);
            if (p != null && !allPlayers.contains(p)) allPlayers.add(p);
        }

        // Calcola MVP (più kill in assoluto)
        UUID mvpUUID = null;
        int maxKills = -1;
        for (Map.Entry<UUID, BedwarsTeam> entry : game.getPlayerTeams().entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p == null) continue;
            int k = plugin.getStatsManager().getKills(p);
            if (k > maxKills) { maxKills = k; mvpUUID = entry.getKey(); }
        }

        // Classifica team per kill
        List<Map.Entry<BedwarsTeam, TeamData>> sorted = game.getTeamDataMap().entrySet().stream()
                .sorted((a, b) -> b.getValue().getKills() - a.getValue().getKills())
                .collect(Collectors.toList());

        for (Player p : allPlayers) {
            p.sendMessage(separator);
            p.sendMessage(MessageUtil.color("        &b&lVERTEX &f&lBEDWARS &8| &eFINE PARTITA"));
            p.sendMessage(separator);

            if (winner != null) {
                p.sendMessage(MessageUtil.color("  &6🏆 VINCITORE: " + winner.getColoredName()));
            } else {
                p.sendMessage(MessageUtil.color("  &7Nessun vincitore (pareggio)"));
            }
            p.sendMessage(empty);
            p.sendMessage(MessageUtil.color("  &e&lClassifica Team:"));

            int pos = 1;
            for (Map.Entry<BedwarsTeam, TeamData> entry : sorted) {
                BedwarsTeam team = entry.getKey();
                TeamData data = entry.getValue();
                String medal = switch (pos) {
                    case 1 -> "&6🥇";
                    case 2 -> "&7🥈";
                    case 3 -> "&c🥉";
                    default -> "&8  " + pos + ".";
                };
                String bedStatus = data.hasBed() ? "&a✔" : "&c✘";
                p.sendMessage(MessageUtil.color("  " + medal + " " + team.getColoredName() +
                        " &8| &fKill: &a" + data.getKills() +
                        " &8| Letto: " + bedStatus));
                pos++;
            }

            p.sendMessage(empty);
            p.sendMessage(MessageUtil.color("  &b&lMVP della partita:"));
            if (mvpUUID != null) {
                Player mvp = Bukkit.getPlayer(mvpUUID);
                if (mvp != null) {
                    BedwarsTeam mvpTeam = game.getTeam(mvp);
                    String teamColor = mvpTeam != null ? mvpTeam.getColor() + "" : "";
                    p.sendMessage(MessageUtil.color("  &6⭐ " + teamColor + mvp.getName() +
                            " &7- &a" + maxKills + " kill"));
                }
            } else {
                p.sendMessage(MessageUtil.color("  &7Nessun dato disponibile."));
            }

            p.sendMessage(empty);
            // Statistiche personali
            BedwarsTeam myTeam = game.getTeam(p);
            if (myTeam != null) {
                int myKills = plugin.getStatsManager().getKills(p);
                int myDeaths = plugin.getStatsManager().getDeaths(p);
                int myBeds = plugin.getStatsManager().getBedsDestroyed(p);
                p.sendMessage(MessageUtil.color("  &f&lLe tue statistiche:"));
                p.sendMessage(MessageUtil.color("  &7Kill: &a" + myKills + " &8| Morti: &c" + myDeaths +
                        " &8| Letti: &d" + myBeds));
            }

            p.sendMessage(separator);
            p.sendMessage(MessageUtil.color("       &7Grazie per aver giocato su &bVertex&f!"));
            p.sendMessage(separator);
        }
    }
}
