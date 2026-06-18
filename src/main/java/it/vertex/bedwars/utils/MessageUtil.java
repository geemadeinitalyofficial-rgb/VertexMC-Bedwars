package it.vertex.bedwars.utils;

import org.bukkit.ChatColor;

public class MessageUtil {
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
