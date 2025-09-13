package net.norius.voteSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VoteTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();

        if(args.length == 1) {
            tab.addAll(List.of("streak", "shop", "pause", "auszahlen"));
            if(sender.hasPermission("votesystem.admin")) {
                tab.add("admin");
            }
        } else if(args.length == 2 && sender.hasPermission("votesystem.admin") && args[0].equalsIgnoreCase("admin")) {
            tab.addAll(List.of("setstreak", "setpoints"));
        } else if(args.length == 3) {
            tab.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return tab.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
