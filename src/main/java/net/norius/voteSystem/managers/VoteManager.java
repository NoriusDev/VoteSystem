package net.norius.voteSystem.managers;

import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.data.dao.VoteDao;
import net.norius.voteSystem.vote.VoteData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class VoteManager {

    private final Map<UUID, VoteData> voteDataMap = new ConcurrentHashMap<>();
    private final VoteSystem plugin;
    private final VoteDao voteDao;

    public VoteManager(VoteSystem plugin) {
        this.plugin = plugin;
        this.voteDao = new VoteDao(plugin);
    }

    public CompletableFuture<VoteData> loadData(UUID uuid, boolean register) {
        return voteDao.loadVoteData(uuid, register)
                .thenApplyAsync(voteData -> {
                    if(voteData != null)
                        voteDataMap.put(uuid, voteData);
                    return voteData;
                });
    }

    public void saveData(UUID uuid) {
        getData(uuid).ifPresent(voteData -> voteDao.saveVoteData(uuid, voteData));
    }

    public void cleanCache(UUID uuid) {
        voteDataMap.remove(uuid);
    }

    public Optional<VoteData> getData(UUID uuid) {
        return voteDataMap.containsKey(uuid) ? Optional.of(voteDataMap.get(uuid)) : Optional.empty();
    }

    public boolean isFull(Player player) {
        return Arrays.stream(player.getInventory().getStorageContents()).noneMatch(
                item -> item == null || item.getType() == Material.AIR || item.isEmpty());
    }

    public void handleVoteStreak(VoteData voteData, int streakPauseHours, int streakResetHours) {
        Instant vote1 = voteData.getLastVote1() != null ? voteData.getLastVote1().toInstant() : null;
        Instant vote2 = voteData.getLastVote2() != null ? voteData.getLastVote2().toInstant() : null;

        Instant lastVote = Stream.of(vote1, vote2)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(Instant.EPOCH);

        if (voteData.getLastPause() != null && voteData.getLastPause().toInstant().isAfter(lastVote)) {
            lastVote = voteData.getLastPause().toInstant().plus(streakPauseHours, ChronoUnit.HOURS);
        }

        if (ChronoUnit.HOURS.between(lastVote, Instant.now()) >= streakResetHours) {
            voteData.setStreak(0);
        }
    }

    public void cleanCacheDelayed(UUID uuid) {
        long delay = 20L * 60 * 5;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Bukkit.getPlayer(uuid) == null && plugin.getVoteManager().getData(uuid).isPresent()) {
                plugin.getVoteManager().cleanCache(uuid);
            }
        }, delay);
    }
}
