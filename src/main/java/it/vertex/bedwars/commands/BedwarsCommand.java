package it.vertex.bedwars.commands;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BedwarsCommand implements CommandExecutor {

    private final VertexBedwars plugin;

    public BedwarsCommand(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Solo i giocatori!"); return true; }
        if (args.length == 0) { sendHelp(player); return true; }

        switch (args[0].toLowerCase()) {
            case "list" -> {
                player.sendMessage(msg("&b--- Arene disponibili ---"));
                var arenas = plugin.getArenaManager().getArenas();
                if (arenas.isEmpty()) { player.sendMessage(msg("&7Nessuna arena disponibile.")); return true; }
                for (var arena : arenas) {
                    BedwarsGame game = plugin.getGameManager().getGame(arena.getName());
                    String status = game == null ? "&aIn attesa" :
                            game.getState() == GameState.IN_GAME ? "&cIn corso" :
                            game.getState() == GameState.STARTING ? "&eIn avvio" : "&aIn attesa";
                    int players = game != null ? game.getTotalPlayers() : 0;
                    player.sendMessage(msg("&e" + arena.getDisplayName() + " &7(" + status + "&7) &f" +
                            players + "/" + arena.getMaxPlayers()));
                }
            }
            case "join" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bw join <arena>")); return true; }
                plugin.getGameManager().joinGame(player, args[1]);
            }
            case "leave" -> plugin.getGameManager().leaveGame(player);
            case "reload" -> {
                if (!player.hasPermission("vertexbedwars.admin")) { player.sendMessage(msg("&cNon hai i permessi!")); return true; }
                plugin.reloadConfig();
                plugin.getArenaManager().loadArenas();
                player.sendMessage(msg("&aPlugin ricaricato!"));
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(msg("&b&l--- VertexBedwars ---"));
        player.sendMessage(msg("&e/bw list &7- Lista arene"));
        player.sendMessage(msg("&e/bw join <arena> &7- Unisciti"));
        player.sendMessage(msg("&e/bw leave &7- Esci"));
        player.sendMessage(msg("&e/bwstats [giocatore] &7- Statistiche"));
        if (player.hasPermission("vertexbedwars.admin"))
            player.sendMessage(msg("&e/bwsetup &7- Configurazione arena &c[ADMIN]"));
    }

    private String msg(String s) {
        return MessageUtil.color(plugin.getConfig().getString("messages.prefix",
                "&b&lVertex &f&lBedwars &8» &r") + s);
    }
}
