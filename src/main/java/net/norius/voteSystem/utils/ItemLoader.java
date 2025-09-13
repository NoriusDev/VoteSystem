package net.norius.voteSystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.norius.voteSystem.VoteSystem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemLoader {

    private final VoteSystem plugin;
    private final List<ShopItem> shopItems = new ArrayList<>();

    public ItemLoader(VoteSystem plugin) {
        this.plugin = plugin;

        loadShopItems();
    }

    public void loadShopItems() {
        shopItems.clear();
        for(Map<?, ?> maps : plugin.getConfig().getMapList("shop-items")) {
            ItemStack itemStack;

            if(maps.containsKey("material"))
                itemStack = new ItemStack(Material.valueOf((String) maps.get("material")));
            else
                itemStack = new ItemStack(Material.BARRIER);

            ItemMeta meta = itemStack.getItemMeta();

            if(maps.containsKey("name"))
                meta.displayName(MiniMessage.miniMessage().deserialize((String) maps.get("name")));
            else
                meta.displayName(Component.text("None displayname defined!", NamedTextColor.RED));

            double price = 0.0;
            if(maps.containsKey("price"))
                price = (double) maps.get("price");

            if(maps.containsKey("amount"))
                itemStack.setAmount((int) maps.get("amount"));

            if(maps.containsKey("enchantments"))
                ((List<String>) maps.get("enchantments")).forEach(s -> {
                    String[] values = s.split(":");
                    meta.addEnchant(Enchantment.getByName(values[0].toUpperCase()), Integer.parseInt(values[1]), true);
                });

            itemStack.setItemMeta(meta);
            shopItems.add(new ShopItem(itemStack.displayName(), price, itemStack));
        }
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }
}
