package net.norius.voteSystem.commands;

import net.norius.voteSystem.managers.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoteCommand implements CommandExecutor {

    private final VoteManager voteManager;

    public VoteCommand(VoteManager voteManager) {
        this.voteManager = voteManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player player)) {
            // no player
            return true;
        }

        if(args.length == 0) {
            // send vote links
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "streak" -> {
                if(args.length == 1) {
                    // send own streak
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    // not found
                    return true;
                }
            }
            case "pause" -> {

            }
            case "shop" -> {

            }
            case "auszahlen" -> {

            }
            case "admin" -> {
                if(args.length < 4) {
                    // wrong usage
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if(target == null) {
                    // player not found
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "setstreak" -> {

                    }
                    case "setpoints" -> {

                    }
                }
            }
        }
    }
}
