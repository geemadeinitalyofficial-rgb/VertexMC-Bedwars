package it.vertex.bedwars.managers;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {

    private final VertexBedwars plugin;
    private final Map<UUID, Scoreboard> playerBoards = new HashMap<>();

    public ScoreboardManager(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    public void updateLobbyBoard(BedwarsGame game, Player player) {
        Scoreboard board = getOrCreate(player);
        Objective obj = board.getObjective("lobby");
        if (obj == null) {
            // Clear old objectives
            for (Objective o : board.getObjectives()) o.unregister();
            obj = board.registerNewObjective("lobby", Criteria.DUMMY,
                    MessageUtil.color("&b&lVERTEX &f&lBEDWARS"));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        obj.getScoreboard().resetScores(ChatColor.GRAY + "");
        set(obj, MessageUtil.color("&7" + game.getArena().getName()), 10);
        set(obj, MessageUtil.color("&fArena: &e" + game.getArena().getDisplayName()), 9);
        set(obj, " ", 8);
        set(obj, MessageUtil.color("&fGiocatori: &a" + game.getTotalPlayers() + "/" + game.getArena().getMaxPlayers()), 7);
        set(obj, "  ", 6);
        if (game.getState() == GameState.STARTING) {
            set(obj, MessageUtil.color("&fInizio in: &e" + game.getCountdownSeconds() + "s"), 5);
        } else {
            set(obj, MessageUtil.color("&fIn attesa di giocatori..."), 5);
        }
        set(obj, "   ", 4);
        set(obj, MessageUtil.color("&bvertex.it"), 3);

        player.setScoreboard(board);
    }

    public void createGameBoard(BedwarsGame game, Player player) {
        Scoreboard board = getOrCreate(player);
        for (Objective o : board.getObjectives()) o.unregister();
        Objective obj = board.registerNewObjective("game", Criteria.DUMMY,
                MessageUtil.color("&b&lVERTEX &f&lBEDWARS"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
        updateGameBoard(game, player);
    }

    public void updateGameBoard(BedwarsGame game, Player player) {
        Scoreboard board = playerBoards.get(player.getUniqueId());
        if (board == null) return;
        Objective obj = board.getObjective("game");
        if (obj == null) return;

        // Clear entries
        for (String entry : board.getEntries()) board.resetScores(entry);

        BedwarsTeam myTeam = game.getTeam(player);
        int line = 14;

        set(obj, " ", line--);
        set(obj, MessageUtil.color("&fTeam: " + (myTeam != null ? myTeam.getColoredName() : "&7-")), line--);
        set(obj, "  ", line--);

        // Show all teams status
        for (BedwarsTeam team : game.getArena().getEnabledTeams()) {
            TeamData data = game.getTeamDataMap().get(team);
            if (data == null) continue;
            String bedStatus = data.hasBed() ? ChatColor.GREEN + "✔" : ChatColor.RED + "✘";
            int members = data.getMembers().size();
            String entry = team.getColor() + team.getDisplayName() + " " + bedStatus +
                    ChatColor.GRAY + " " + members;
            set(obj, MessageUtil.color(entry), line--);
        }

        set(obj, "   ", line--);
        set(obj, MessageUtil.color("&fK: &a" + (myTeam != null ? game.getTeamDataMap().get(myTeam).getKills() : 0)), line--);
        set(obj, "    ", line--);

        // Game time
        int mins = game.getGameTimeSeconds() / 60;
        int secs = game.getGameTimeSeconds() % 60;
        set(obj, MessageUtil.color("&fTempo: &e" + String.format("%02d:%02d", mins, secs)), line--);
        set(obj, "     ", line--);
        set(obj, MessageUtil.color("&bvertex.it"), line--);
    }

    private void set(Objective obj, String text, int score) {
        Score s = obj.getScore(text);
        s.setScore(score);
    }

    private Scoreboard getOrCreate(Player player) {
        return playerBoards.computeIfAbsent(player.getUniqueId(),
                k -> Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void removePlayer(Player player) {
        playerBoards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
