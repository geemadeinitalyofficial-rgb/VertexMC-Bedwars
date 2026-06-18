package it.vertex.bedwars.commands;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {
    private final VertexBedwars plugin;
    public JoinCommand(VertexBedwars plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Solo i giocatori!"); return true; }
        if (args.length == 0) {
            player.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.prefix") + "&cUso: /bwjoin <arena>"));
            return true;
        }
        plugin.getGameManager().joinGame(player, args[0]);
        return true;
    }
}
