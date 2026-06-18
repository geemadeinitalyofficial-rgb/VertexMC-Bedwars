package it.vertex.bedwars.commands;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.managers.StatsManager;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    private final VertexBedwars plugin;
    public StatsCommand(VertexBedwars plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player asker)) { sender.sendMessage("Solo i giocatori!"); return true; }

        Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : asker;
        if (target == null) {
            asker.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.prefix") + "&cGiocatore non trovato!"));
            return true;
        }

        StatsManager stats = plugin.getStatsManager();
        asker.sendMessage(MessageUtil.color("&b&l--- Stats di &f" + target.getName() + " &b---"));
        asker.sendMessage(MessageUtil.color("&ePartite giocate: &f" + stats.getGamesPlayed(target)));
        asker.sendMessage(MessageUtil.color("&aVittorie: &f" + stats.getWins(target)));
        asker.sendMessage(MessageUtil.color("&cSconfitte: &f" + stats.getLosses(target)));
        asker.sendMessage(MessageUtil.color("&6Kill: &f" + stats.getKills(target)));
        asker.sendMessage(MessageUtil.color("&7Morti: &f" + stats.getDeaths(target)));
        asker.sendMessage(MessageUtil.color("&bK/D: &f" + String.format("%.2f", stats.getKDR(target))));
        asker.sendMessage(MessageUtil.color("&dLetti distrutti: &f" + stats.getBedsDestroyed(target)));
        return true;
    }
}
