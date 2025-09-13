package net.norius.voteSystem.commands;

import net.kyori.adventure.text.Component;
import net.norius.voteSystem.VoteSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VoteSystemReloadCommand implements CommandExecutor {

    private final VoteSystem plugin;

    public VoteSystemReloadCommand(VoteSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(plugin.getConfigLoader().getPrefix().append(Component.text(" "))
                .append(Component.text("§aLade Konfiguration neu...")));

        try {
            plugin.reloadConfig();
            plugin.getConfigLoader().reload();
            plugin.getJoinQuitListener().reload();
            plugin.getVoteListener().reload();
            plugin.getItemLoader().loadShopItems();
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigLoader().getPrefix().append(Component.text(" "))
                    .append(Component.text("§cEs gab einen Fehler beim Laden der Konfiguration!")));
            return true;
        }

        sender.sendMessage(plugin.getConfigLoader().getPrefix().append(Component.text(" "))
                .append(Component.text("§aKonfiguration erfolgreich neugeladen!")));
        return true;
    }
}
