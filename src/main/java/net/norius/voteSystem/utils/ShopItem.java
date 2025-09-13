package net.norius.voteSystem.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public record ShopItem(Component name, double price, ItemStack item) {

}
