package net.norius.voteSystem;

import net.norius.voteSystem.data.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoteSystem extends JavaPlugin {

    private Database database;

    @Override
    public void onEnable() {
        database = new Database(this);
    }

    @Override
    public void onDisable() {
        if(database != null && database.getDataSource() != null && database.getDataSource().isRunning())
            database.getDataSource().close();
    }

    public Database getDatabase() {
        return database;
    }
}
