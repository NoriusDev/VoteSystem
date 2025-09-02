package net.norius.voteSystem;

import net.norius.voteSystem.commands.VoteCommand;
import net.norius.voteSystem.data.Database;
import net.norius.voteSystem.hook.PlaceholderApiHook;
import net.norius.voteSystem.listeners.JoinQuitListener;
import net.norius.voteSystem.managers.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VoteSystem extends JavaPlugin {

    private Database database;
    private VoteManager voteManager;

    @Override
    public void onEnable() {
        loadConfig();

        database = new Database(this);
        voteManager = new VoteManager(this);

        register();
    }

    @Override
    public void onDisable() {
        if(database != null && database.getDataSource() != null && database.getDataSource().isRunning())
            database.getDataSource().close();
    }

    private void register() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new JoinQuitListener(this), this);

        Objects.requireNonNull(getCommand("vote")).setExecutor(new VoteCommand(voteManager));

        if(Bukkit.getPluginManager().getPlugin("PlaceholderApi") != null) {
            new PlaceholderApiHook(this).register();
        }
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public Database getDatabase() {
        return database;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }
}
