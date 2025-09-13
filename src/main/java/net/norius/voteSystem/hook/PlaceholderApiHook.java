package net.norius.voteSystem.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.vote.VoteData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlaceholderApiHook extends PlaceholderExpansion {

    private final VoteSystem plugin;

    public PlaceholderApiHook(VoteSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vote";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        UUID uuid = player.getUniqueId();

        Optional<VoteData> data = plugin.getVoteManager().getData(uuid);

        if (data.isEmpty()) {
            plugin.getVoteManager().loadData(uuid, false);
            plugin.getVoteManager().cleanCacheDelayed(uuid);
            return "";
        }

        VoteData voteData = data.get();

        return switch (params.toLowerCase()) {
            case "playerstreaknumber" -> String.valueOf(voteData.getStreak());
            case "playerpoints" -> String.valueOf(voteData.getPoints());
            default -> "";
        };
    }

}
