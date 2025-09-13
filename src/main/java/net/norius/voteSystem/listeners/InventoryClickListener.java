package net.norius.voteSystem.listeners;

import net.norius.voteSystem.gui.ShopMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        if(!(event.getView().getTopInventory().getHolder() instanceof ShopMenu menu)) return;

        if(event.getClickedInventory().equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            menu.handleClick(event);
        }
    }
}
