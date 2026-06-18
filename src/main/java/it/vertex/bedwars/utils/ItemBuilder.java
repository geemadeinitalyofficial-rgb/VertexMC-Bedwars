package it.vertex.bedwars.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder named(Material material, String name) {
        return new ItemBuilder(material).name(name);
    }

    public ItemBuilder name(String name) {
        meta.setDisplayName(MessageUtil.color(name));
        return this;
    }

    public ItemBuilder lore(String... lore) {
        List<String> colored = Arrays.stream(lore)
                .map(MessageUtil::color)
                .collect(Collectors.toList());
        meta.setLore(colored);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
