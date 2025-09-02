package net.norius.voteSystem.managers;

import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.data.dao.VoteDao;
import net.norius.voteSystem.vote.VoteData;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class VoteManager {

    private final Map<UUID, VoteData> voteDataMap = new ConcurrentHashMap<>();
    private final VoteSystem plugin;
    private final VoteDao voteDao;

    public VoteManager(VoteSystem plugin) {
        this.plugin = plugin;
        this.voteDao = new VoteDao(plugin);
    }

    public CompletableFuture<VoteData> loadData(UUID uuid) {
        return voteDao.loadVoteData(uuid)
                .thenApply(voteData -> {
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
}
