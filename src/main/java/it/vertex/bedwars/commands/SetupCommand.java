package it.vertex.bedwars.commands;

import it.vertex.bedwars.VertexBedwars;
import it.vertex.bedwars.game.*;
import it.vertex.bedwars.utils.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    private final VertexBedwars plugin;

    public SetupCommand(VertexBedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Solo i giocatori!"); return true; }
        if (!player.hasPermission("vertexbedwars.admin")) {
            player.sendMessage(msg("&cNon hai i permessi!"));
            return true;
        }
        if (args.length == 0) { sendHelp(player); return true; }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup create <nome>")); return true; }
                String name = args[1];
                if (plugin.getArenaManager().arenaExists(name)) {
                    player.sendMessage(msg("&cArena già esistente!")); return true;
                }
                plugin.getArenaManager().createArena(name);
                player.sendMessage(msg("&aArena &e" + name + " &acreata! Ora configurala."));
            }
            case "setlobby" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup setlobby <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.setLobbySpawn(player.getLocation());
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aSpawn lobby impostato per &e" + arena.getName() + "&a!"));
            }
            case "setspawn" -> {
                if (args.length < 3) { player.sendMessage(msg("&cUso: /bwsetup setspawn <arena> <team>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                try {
                    BedwarsTeam team = BedwarsTeam.valueOf(args[2].toUpperCase());
                    arena.getTeamSpawns().put(team, player.getLocation());
                    plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(msg("&aSpawn impostato per team &e" + team.getDisplayName() + "&a!"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(msg("&cTeam non valido! Team disponibili: RED, BLUE, GREEN, YELLOW, AQUA, WHITE, PINK, GRAY"));
                }
            }
            case "setbed" -> {
                if (args.length < 3) { player.sendMessage(msg("&cUso: /bwsetup setbed <arena> <team>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                try {
                    BedwarsTeam team = BedwarsTeam.valueOf(args[2].toUpperCase());
                    arena.getBedLocations().put(team, player.getLocation());
                    plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(msg("&aLetto impostato per team &e" + team.getDisplayName() + "&a!"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(msg("&cTeam non valido!"));
                }
            }
            case "adddiamondgen" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup adddiamondgen <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.getDiamondGenerators().add(player.getLocation());
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aGeneratore diamante aggiunto!"));
            }
            case "addemeraldgen" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup addemeraldgen <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.getEmeraldGenerators().add(player.getLocation());
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aGeneratore smeraldo aggiunto!"));
            }
            case "addshop" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup addshop <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.getShopVillagers().add(player.getLocation());
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aVillager shop aggiunto!"));
            }
            case "addupgrade" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup addupgrade <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.getUpgradeVillagers().add(player.getLocation());
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aVillager upgrade aggiunto!"));
            }
            case "setteams" -> {
                if (args.length < 3) { player.sendMessage(msg("&cUso: /bwsetup setteams <arena> <numero>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                try {
                    int teams = Integer.parseInt(args[2]);
                    if (teams < 2 || teams > 8) { player.sendMessage(msg("&cTeam: tra 2 e 8.")); return true; }
                    arena.setMaxTeams(teams);
                    plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(msg("&aNumero team impostato a &e" + teams + "&a!"));
                } catch (NumberFormatException e) {
                    player.sendMessage(msg("&cNumero non valido!"));
                }
            }
            case "setsize" -> {
                if (args.length < 3) { player.sendMessage(msg("&cUso: /bwsetup setsize <arena> <giocatori-per-team>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                try {
                    int size = Integer.parseInt(args[2]);
                    arena.setPlayersPerTeam(size);
                    plugin.getArenaManager().saveArena(arena);
                    player.sendMessage(msg("&aDimensione team impostata a &e" + size + "&a!"));
                } catch (NumberFormatException e) {
                    player.sendMessage(msg("&cNumero non valido!"));
                }
            }
            case "setspectator" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup setspectator <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.setSpectatorSpawn(player.getLocation());
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aSpawn spettatore impostato!"));
            }
            case "done" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup done <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                arena.setConfigured(true);
                plugin.getArenaManager().saveArena(arena);
                player.sendMessage(msg("&aArena &e" + arena.getName() + " &aconfigurata e pronta!"));
            }
            case "info" -> {
                if (args.length < 2) { player.sendMessage(msg("&cUso: /bwsetup info <arena>")); return true; }
                Arena arena = getArena(player, args[1]); if (arena == null) return true;
                player.sendMessage(msg("&b--- Info Arena: &e" + arena.getName() + " &b---"));
                player.sendMessage(msg("&7Team: &f" + arena.getMaxTeams() + " | Giocatori/team: &f" + arena.getPlayersPerTeam()));
                player.sendMessage(msg("&7Spawn lobby: &f" + (arena.getLobbySpawn() != null ? "✔" : "✘")));
                player.sendMessage(msg("&7Gen Diamante: &f" + arena.getDiamondGenerators().size()));
                player.sendMessage(msg("&7Gen Smeraldo: &f" + arena.getEmeraldGenerators().size()));
                player.sendMessage(msg("&7Shop: &f" + arena.getShopVillagers().size() + " | Upgrade: &f" + arena.getUpgradeVillagers().size()));
                player.sendMessage(msg("&7Configurata: " + (arena.isConfigured() ? "&a✔" : "&c✘")));
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private Arena getArena(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) { player.sendMessage(msg("&cArena non trovata: &e" + name)); return null; }
        return arena;
    }

    private void sendHelp(Player player) {
        player.sendMessage(msg("&b&l--- Setup BedWars ---"));
        player.sendMessage(msg("&e/bwsetup create <nome> &7- Crea arena"));
        player.sendMessage(msg("&e/bwsetup setlobby <arena> &7- Spawn lobby (dove sei)"));
        player.sendMessage(msg("&e/bwsetup setspawn <arena> <team> &7- Spawn team"));
        player.sendMessage(msg("&e/bwsetup setbed <arena> <team> &7- Posizione letto"));
        player.sendMessage(msg("&e/bwsetup adddiamondgen <arena> &7- Aggiunge gen diamante"));
        player.sendMessage(msg("&e/bwsetup addemeraldgen <arena> &7- Aggiunge gen smeraldo"));
        player.sendMessage(msg("&e/bwsetup addshop <arena> &7- Aggiunge villager shop"));
        player.sendMessage(msg("&e/bwsetup addupgrade <arena> &7- Aggiunge villager upgrade"));
        player.sendMessage(msg("&e/bwsetup setteams <arena> <num> &7- Numero team (2-8)"));
        player.sendMessage(msg("&e/bwsetup setsize <arena> <num> &7- Giocatori per team"));
        player.sendMessage(msg("&e/bwsetup setspectator <arena> &7- Spawn spettatori"));
        player.sendMessage(msg("&e/bwsetup done <arena> &7- Segna come configurata"));
        player.sendMessage(msg("&e/bwsetup info <arena> &7- Info arena"));
    }

    private String msg(String s) {
        return MessageUtil.color(plugin.getConfig().getString("messages.prefix",
                "&b&lVertex &f&lBedwars &8» &r") + s);
    }
}
