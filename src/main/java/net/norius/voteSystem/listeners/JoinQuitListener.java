package net.norius.voteSystem.listeners;

import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.utils.VoteSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class JoinQuitListener implements Listener {

    private final VoteSystem plugin;

    private volatile VoteSettings settings;

    public JoinQuitListener(VoteSystem plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        this.settings = new VoteSettings(
                plugin.getConfig().getInt("settings.streak-reset-hours"),
                plugin.getConfig().getInt("settings.streak-pause-hours"),
                plugin.getConfig().getDouble("settings.interest.points"),
                plugin.getConfig().getDouble("settings.interest.required-bank-points"),
                plugin.getConfig().getInt("settings.interest.required-streak"),
                plugin.getConfig().getInt("settings.interest.intervall-days")
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(plugin.getVoteManager().getData(uuid).isEmpty()) {
            plugin.getVoteManager().loadData(uuid, true).thenAcceptAsync(voteData -> {
                Instant current = Instant.now();

                plugin.getVoteManager().handleVoteStreak(voteData, settings.streakPauseHours(), settings.streakResetHours());

                if(voteData.getStreak() >= settings.requiredStreak() && voteData.getBank() >= settings.requiredBankPoints()) {
                    long daysAfterInterest = voteData.getLastInterest() == null ? settings.intervallDays() :
                            ChronoUnit.DAYS.between(voteData.getLastInterest().toInstant(), current);

                    if(daysAfterInterest < settings.intervallDays()) return;
                    long interestTimes = Math.floorDiv(daysAfterInterest, settings.intervallDays());
                    long bankPointsMultiplikator = (int) Math.floor(voteData.getBank() / settings.requiredBankPoints());

                    voteData.setBank(voteData.getBank() + (interestTimes * bankPointsMultiplikator * settings.interestPoints()));

                    Instant lastInterestInstant = current.minus(settings.intervallDays() * interestTimes, ChronoUnit.DAYS);
                    voteData.setLastInterest(Date.from(lastInterestInstant));
                }
            }).thenRun(() -> plugin.getVoteManager().saveData(uuid));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getVoteManager().cleanCache(player.getUniqueId());
    }

    public VoteSettings getSettings() {
        return settings;
    }
}
