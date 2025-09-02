package net.norius.voteSystem.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.norius.voteSystem.VoteSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class Database {

    private final HikariDataSource dataSource;
    private final VoteSystem plugin;
    private final ExecutorService executor;

    public Database(VoteSystem plugin) {
        this.plugin = plugin;

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s",
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getInt("database.port"),
                plugin.getConfig().getString("database.database")));
        config.setUsername(plugin.getConfig().getString("database.username"));
        config.setPassword(plugin.getConfig().getString("database.password"));

        this.dataSource = new HikariDataSource(config);
        this.executor = Executors.newCachedThreadPool();

        loadTables();
    }

    private void loadTables() {
        CompletableFuture.runAsync(() -> {
            try(Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS votesystem_votes(" +
                        "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                        "streak INT DEFAULT 0, " +
                        "points INT DEFAULT 0, " +
                        "bank INT DEFAULT 0, " +
                        "last_vote_1 LONG DEFAULT 0, " +
                        "last_vote_2 LONG DEFAULT 0, " +
                        "active_vote_days INT DEFAULT 0, " +
                        "interest_points INT DEFAULT 0, " +
                        "last_interest LONG DEFAULT 0)")) {
                statement.execute();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create table votesystem_votes", e);
            }
        }, executor);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
