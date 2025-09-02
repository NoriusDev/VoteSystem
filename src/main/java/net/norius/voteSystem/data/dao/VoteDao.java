package net.norius.voteSystem.data.dao;

import net.norius.voteSystem.VoteSystem;
import net.norius.voteSystem.vote.VoteData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class VoteDao {

    private final VoteSystem plugin;

    public VoteDao(VoteSystem plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<VoteData> loadVoteData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = plugin.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM votesystem_votes WHERE uuid = ?")) {
                statement.setString(1, uuid.toString());

                try(ResultSet resultSet = statement.executeQuery()) {
                    return new VoteData(
                            resultSet.getInt("streak"),
                            resultSet.getInt("points"),
                            resultSet.getInt("bank"),
                            new Date(resultSet.getLong("last_vote_1")),
                            new Date(resultSet.getLong("last_vote_2")),
                            resultSet.getInt("active_vote_days"),
                            resultSet.getInt("interest_points"),
                            new Date(resultSet.getLong("last_vote")),
                            new Date(resultSet.getLong("last_pause"))
                    );
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Could not load vote data!", e);
            }

            return null;
        }, plugin.getDatabase().getExecutor());
    }

    public void saveVoteData(UUID uuid, VoteData voteData) {
        CompletableFuture.runAsync(() -> {
            try(Connection connection = plugin.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO votesystem_votes(" +
                        "uuid, streak, points, bank, last_vote_1, last_vote_2, active_vote_days, interest_points, last_interest, last_pause) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                        "streak = VALUES(streak), " +
                        "points = VALUES(points), " +
                        "bank = VALUES(bank), " +
                        "last_vote_1 = VALUES(last_vote_1), " +
                        "last_vote_2 = VALUES(last_vote_2), " +
                        "active_vote_days = VALUES(active_vote_days), " +
                        "interest_points = VALUES(interest_points), " +
                        "last_interest = VALUES(last_interest), " +
                        "last_pause = VALUES(last_pause)")) {

                statement.setString(1, uuid.toString());
                statement.setInt(2, voteData.getStreak());
                statement.setInt(3, voteData.getPoints());
                statement.setInt(4, voteData.getBank());
                statement.setLong(5, voteData.getLastVote1().getTime());
                statement.setLong(6, voteData.getLastVote2().getTime());
                statement.setInt(7, voteData.getActiveVoteDays());
                statement.setInt(8, voteData.getInterestPoints());
                statement.setLong(9, voteData.getLastInterest().getTime());
                statement.setLong(10, voteData.getLastPause().getTime());

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save vote data!", e);
            }
        }, plugin.getDatabase().getExecutor());
    }
}
