package net.norius.voteSystem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.gui.ShopMenu;
import net.norius.voteSystem.vote.VoteData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VoteCommand implements CommandExecutor {

    private final VoteSystem plugin;

    public VoteCommand(VoteSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigLoader().get("general.no-player", true));
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(plugin.getConfigLoader().getPrefix().append(MiniMessage.miniMessage().deserialize(plugin.getConfigLoader().getString("vote.link-1")
                            .replace("%link-1%", Objects.requireNonNull(plugin.getConfig().getString("settings.vote-link-1")) + player.getName()))
                    .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
            player.sendMessage(plugin.getConfigLoader().getPrefix().append(MiniMessage.miniMessage().deserialize(plugin.getConfigLoader().getString("vote.link-2")
                            .replace("%link-2%", Objects.requireNonNull(plugin.getConfig().getString("settings.vote-link-2"))))
                    .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
            return true;
        }

        plugin.getVoteManager().getData(player.getUniqueId()).ifPresentOrElse(voteData -> {
            switch (args[0].toLowerCase()) {
                case "streak" -> {
                    if (args.length == 1) {
                        player.sendMessage(plugin.getConfigLoader().get("vote.own-streak",
                                List.of("streak"), List.of(Component.text(voteData.getStreak())), true));
                        return;
                    }

                    if(args.length != 2) {
                        player.sendMessage(plugin.getConfigLoader().get("general.usage", true));
                        return;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(plugin.getConfigLoader().get("general.not-found", true));
                        return;
                    }

                    plugin.getVoteManager().getData(target.getUniqueId()).ifPresent(targetData ->
                            player.sendMessage(plugin.getConfigLoader().get("vote.other-streak", List.of("player", "streak"),
                                    List.of(target.displayName(), Component.text(targetData.getStreak())), true)));
                }
                case "pause" -> {
                    int days = plugin.getConfig().getInt("settings.pause-cooldown-days");
                    if(voteData.getLastPause() != null && ChronoUnit.DAYS.between(LocalDate.ofInstant(voteData.getLastPause().toInstant(), ZoneId.systemDefault()), LocalDate.now()) < days) {
                        Date date = Date.from(ChronoUnit.DAYS.addTo(voteData.getLastPause().toInstant(), days));
                        player.sendMessage(plugin.getConfigLoader().get("vote.pause-cooldown", List.of("date"), List.of(Component.text(
                                new SimpleDateFormat(Objects.requireNonNull(plugin.getConfig().getString("settings.date-format"))).format(date))), true));
                        return;
                    }

                    voteData.setLastPause(new Date(System.currentTimeMillis()));
                    plugin.getVoteManager().saveData(player.getUniqueId());
                    player.sendMessage(plugin.getConfigLoader().get("vote.paused", true));
                }
                case "shop" -> player.openInventory(new ShopMenu(player, plugin).create());
                case "auszahlen" -> {
                    if(args.length != 2) {
                        player.sendMessage(plugin.getConfigLoader().get("general.usage", true));
                        return;
                    }

                    int minStreak = plugin.getConfig().getInt("settings.min-streak-access");
                    if(voteData.getStreak() < minStreak) {
                        player.sendMessage(plugin.getConfigLoader().get("vote.locked-bank", List.of("streak"), List.of(Component.text(minStreak)), true));
                        return;
                    }

                    if(!checkPoints(args[1])) {
                        player.sendMessage(plugin.getConfigLoader().get("vote.wrong-double", true));
                        return;
                    }

                    double amount = Double.parseDouble(args[1]);

                    if(voteData.getBank() < amount) {
                        player.sendMessage(plugin.getConfigLoader().get("vote.not-enough-bank", true));
                        return;
                    }

                    double minWithdraw = plugin.getConfig().getDouble("settings.min-withdraw-points");

                    if(amount < minWithdraw) {
                        player.sendMessage(plugin.getConfigLoader().get("vote.min-withdraw-points", List.of("points"), List.of(Component.text(minWithdraw)), true));
                        return;
                    }

                    voteData.setPoints(voteData.getPoints() + amount);
                    voteData.setBank(voteData.getBank() - amount);
                    plugin.getVoteManager().saveData(player.getUniqueId());

                    player.sendMessage(plugin.getConfigLoader().get("bank-withdrew", List.of("points"), List.of(Component.text(amount)), true));
                }
                case "admin" -> {
                    if(!player.hasPermission("vote.admin")) {
                        player.sendMessage(plugin.getConfigLoader().get("general.no-perms", true));
                        return;
                    }

                    if (args.length < 4) {
                        player.sendMessage(plugin.getConfigLoader().get("general.admin-usage", true));
                        return;
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
                    UUID uuid = offlinePlayer.getUniqueId();

                    plugin.getVoteManager().getData(uuid).ifPresentOrElse(targetData ->
                            handleSubCmds(args, player, offlinePlayer, targetData), () ->
                            plugin.getVoteManager().loadData(uuid, false).thenAcceptAsync(offlineData -> {
                                if(offlineData == null) {
                                    runSync(() -> player.sendMessage(plugin.getConfigLoader().get("vote.general.not-found", true)));
                                    return;
                                }

                                handleSubCmds(args, player, offlinePlayer, offlineData);
                                plugin.getVoteManager().saveData(uuid);
                                plugin.getVoteManager().cleanCacheDelayed(uuid);
                            }));
                }
            }
        }, () -> player.sendMessage(plugin.getConfigLoader().get("vote.no-data", true)));

        return true;
    }

    private void handleSubCmds(String[] args, Player player, OfflinePlayer offlinePlayer, VoteData voteData) {
        switch (args[1].toLowerCase()) {
            case "setstreak" -> {
                try {
                    int streak = Integer.parseInt(args[3]);

                    if(streak < 0) {
                        runSync(() -> player.sendMessage(plugin.getConfigLoader().get("vote.wrong-int", true)));
                        return;
                    }

                    voteData.setStreak(streak);
                    plugin.getVoteManager().saveData(offlinePlayer.getUniqueId());
                    runSync(() -> player.sendMessage(plugin.getConfigLoader().get("admin.set-streak", List.of("player", "streak"),
                            List.of(Component.text(offlinePlayer.getName()), Component.text(streak)), true)));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getConfigLoader().get("vote.wrong-int", true));
                }
            }
            case "setpoints" -> {
                if(!checkPoints(args[3])) {
                    runSync(() -> player.sendMessage(plugin.getConfigLoader().get("vote.wrong-double", true)));
                    return;
                }

                double points = Double.parseDouble(args[3]);
                voteData.setPoints(points);
                plugin.getVoteManager().saveData(offlinePlayer.getUniqueId());
                runSync(() -> player.sendMessage(plugin.getConfigLoader().get("admin.set-points", List.of("player", "points"),
                        List.of(Component.text(offlinePlayer.getName()), Component.text(points)), true)));
            }
        }
    }

    private void runSync(Runnable action) {
        Bukkit.getScheduler().runTask(plugin, action);
    }

    private boolean checkPoints(String input) {
        try {
            double amount = Double.parseDouble(input);

            if(amount < 0 || Math.floor(amount * 10) != amount * 10) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
