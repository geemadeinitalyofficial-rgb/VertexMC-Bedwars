package it.vertex.bedwars.commands;

import it.vertex.bedwars.VertexBedwars;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {
    private final VertexBedwars plugin;
    public LeaveCommand(VertexBedwars plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Solo i giocatori!"); return true; }
        plugin.getGameManager().leaveGame(player);
        return true;
    }
}
