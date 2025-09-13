package net.norius.voteSystem.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.vote.VoteData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class VoteListener implements Listener {

    private final VoteSystem plugin;

    private volatile int minStreakPoints;
    private volatile double voteRewardPoints;
    private volatile String voteCommand;

    public VoteListener(VoteSystem plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        this.minStreakPoints = plugin.getConfig().getInt("settings.min-streak-points");
        this.voteRewardPoints = plugin.getConfig().getDouble("settings.vote-reward-points");
        this.voteCommand = plugin.getConfig().getString("settings.vote-command");
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vote.getUsername());
        UUID uuid = offlinePlayer.getUniqueId();
        int voteSite = vote.getAddress().contains("minecraft-serverlist") ? 1 : 2;

        plugin.getVoteManager().getData(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> plugin.getVoteManager().loadData(uuid, false))
                .thenAcceptAsync(voteData -> handleVote(voteData, offlinePlayer, voteSite))
                .thenRunAsync(() -> {
                    plugin.getVoteManager().saveData(uuid);
                    plugin.getVoteManager().cleanCacheDelayed(uuid);
                });

    }

    private void handleVote(VoteData voteData, OfflinePlayer offlinePlayer, int voteSite) {
        if(voteData == null) return;

        if(offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if(player != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), voteCommand.replace("%player%", player.getName()));
                    // TODO: message to player?
                });
            }
        }

        plugin.getVoteManager().handleVoteStreak(voteData,
                plugin.getJoinQuitListener().getSettings().streakPauseHours(),
                plugin.getJoinQuitListener().getSettings().streakResetHours());

        Instant current = Instant.now();

        Instant last1 = voteData.getLastVote1() != null ? voteData.getLastVote1().toInstant() : null;
        Instant last2 = voteData.getLastVote2() != null ? voteData.getLastVote2().toInstant() : null;

        boolean incrementStreak = Stream.of(last1, last2)
                .allMatch(ts -> ts == null || !isAfter(ts, current));

        if (incrementStreak) {
            voteData.setStreak(voteData.getStreak() + 1);
        }

        if(voteSite == 1)
            voteData.setLastVote1(new Date(System.currentTimeMillis()));
        else
            voteData.setLastVote2(new Date(System.currentTimeMillis()));

        if(voteData.getStreak() >= minStreakPoints)
            voteData.setPoints(voteData.getPoints() + voteRewardPoints);
    }

    private boolean isAfter(Instant one, Instant two) {
        return LocalDate.ofInstant(one, ZoneId.systemDefault()).plusDays(1)
                .isAfter(LocalDate.ofInstant(two, ZoneId.systemDefault()));
    }
}
