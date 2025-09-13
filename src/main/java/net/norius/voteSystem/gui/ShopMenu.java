package net.norius.voteSystem.gui;

import net.kyori.adventure.text.Component;
import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.utils.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ShopMenu implements InventoryHolder {

    private final Player player;
    private final VoteSystem plugin;

    private Inventory inventory;

    public ShopMenu(Player player, VoteSystem plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(this, calculateInvSize(), plugin.getConfigLoader().get("shop.title", false));

        setItems(inventory);

        this.inventory = inventory;
        return inventory;
    }

    private void setItems(Inventory inv) {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        glass.editMeta(itemMeta -> itemMeta.displayName(Component.empty()));

        int count = 0;
        for(int i = 0; i < inv.getSize(); i++) {
            if(isBorderSlot(i, inv))
                inv.setItem(i, glass);
            else {
                if(plugin.getItemLoader().getShopItems().size() <= count) continue;

                ShopItem shopItem = plugin.getItemLoader().getShopItems().get(count);
                ItemStack item = shopItem.item().clone();

                item.editMeta(itemMeta -> itemMeta.lore(plugin.getConfigLoader().getList("shop.item-lore",
                        List.of("price"), List.of(Component.text(shopItem.price())))));

                inv.setItem(i, item);
                count++;
            }
        }

        plugin.getVoteManager().getData(player.getUniqueId()).ifPresent(voteData -> {
            ItemStack cash = new ItemStack(Material.RAW_GOLD);
            cash.editMeta(itemMeta -> itemMeta.displayName(plugin.getConfigLoader().get("shop.cash",
                    List.of("points"), List.of(Component.text(voteData.getPoints())), false)));

            ItemStack bank = new ItemStack(Material.GOLD_BLOCK);
            bank.editMeta(itemMeta -> itemMeta.displayName(plugin.getConfigLoader().get("shop.bank",
                    List.of("bank"), List.of(Component.text(voteData.getBank())), false)));

            inv.setItem(inv.getSize() - 1, cash);
            inv.setItem(inv.getSize() - 9, bank);
        });
    }

    public void handleClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();

        if(isBorderSlot(event.getSlot(), event.getInventory())) {
            return;
        }

        getShopItem(event.getSlot(), event.getInventory())
                .ifPresent(shopItem -> plugin.getVoteManager().getData(player.getUniqueId()).ifPresent(voteData -> {
                    if(voteData.getPoints() < shopItem.price()) {
                        player.sendMessage(plugin.getConfigLoader().get("vote.not-enough-points", true));
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        return;
                    }

                    if(plugin.getVoteManager().isFull(player)) {
                        player.sendMessage(plugin.getConfigLoader().get("shop.inv-full", true));
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        return;
                    }

                    player.getInventory().addItem(shopItem.item());
                    voteData.setPoints(voteData.getPoints() - shopItem.price());

                    player.sendMessage(plugin.getConfigLoader().get("shop.received", List.of("item", "price"),
                            List.of(shopItem.name(), Component.text(shopItem.price())), true));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    plugin.getVoteManager().saveData(player.getUniqueId());
                }));
    }

    private Optional<ShopItem> getShopItem(int slot, Inventory inv) {
        int count = 0;
        for(int i = 0; i < inv.getSize(); i++) {
            if(i == slot)
                return Optional.of(plugin.getItemLoader().getShopItems().get(count));

            if(!isBorderSlot(i, inv))
                count ++;
        }
        return Optional.empty();
    }

    private boolean isBorderSlot(int slot, Inventory inv) {
        return slot < 9 || slot > inv.getSize() - 9 || slot % 9 == 0 || (slot + 1) % 9 == 0;
    }

    private int calculateInvSize() {
        int items = plugin.getItemLoader().getShopItems().size();
        int size = items == 0 ? 7 : Math.min(items, 28);

        return 18 + 9 * Math.ceilDiv(size, 7);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
