package net.norius.voteSystem.listeners;

import net.norius.voteSystem.VoteSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final VoteSystem plugin;

    public JoinQuitListener(VoteSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(plugin.getVoteManager().getData(player.getUniqueId()).isEmpty()) {
            plugin.getVoteManager().loadData(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getVoteManager().cleanCache(player.getUniqueId());
    }
}
