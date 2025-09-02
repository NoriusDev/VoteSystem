package net.norius.voteSystem.managers;

import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.vote.VoteData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoteManager {

    private final Map<UUID, VoteData> voteDataMap = new ConcurrentHashMap<>();
    private final VoteSystem plugin;

    public VoteManager(VoteSystem plugin) {
        this.plugin = plugin;
    }


}
