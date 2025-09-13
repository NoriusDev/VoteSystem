package net.norius.voteSystem;

import net.norius.voteSystem.commands.VoteCommand;
import net.norius.voteSystem.commands.VoteSystemReloadCommand;
import net.norius.voteSystem.commands.VoteTabCompleter;
import net.norius.voteSystem.data.Database;
import net.norius.voteSystem.hook.PlaceholderApiHook;
import net.norius.voteSystem.listeners.InventoryClickListener;
import net.norius.voteSystem.listeners.JoinQuitListener;
import net.norius.voteSystem.listeners.VoteListener;
import net.norius.voteSystem.managers.VoteManager;
import net.norius.voteSystem.utils.ConfigLoader;
import net.norius.voteSystem.utils.ItemLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VoteSystem extends JavaPlugin {

    private Database database;
    private VoteManager voteManager;

    private ConfigLoader configLoader;
    private ItemLoader itemLoader;

    private VoteListener voteListener;
    private JoinQuitListener joinQuitListener;

    @Override
    public void onEnable() {
        loadConfig();

        configLoader = new ConfigLoader(this);
        database = new Database(this);
        voteManager = new VoteManager(this);
        itemLoader = new ItemLoader(this);

        register();
    }

    @Override
    public void onDisable() {
        if(database != null && database.getDataSource() != null && database.getDataSource().isRunning())
            database.getDataSource().close();
    }

    private void register() {
        PluginManager pm = Bukkit.getPluginManager();

        this.joinQuitListener = new JoinQuitListener(this);
        this.voteListener = new VoteListener(this);

        pm.registerEvents(joinQuitListener, this);
        pm.registerEvents(voteListener, this);
        pm.registerEvents(new InventoryClickListener(), this);

        Objects.requireNonNull(getCommand("vote")).setExecutor(new VoteCommand(this));
        Objects.requireNonNull(getCommand("vote")).setTabCompleter(new VoteTabCompleter());
        Objects.requireNonNull(getCommand("votesystemreload")).setExecutor(new VoteSystemReloadCommand(this));

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

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public ItemLoader getItemLoader() {
        return itemLoader;
    }

    public JoinQuitListener getJoinQuitListener() {
        return joinQuitListener;
    }

    public VoteListener getVoteListener() {
        return voteListener;
    }
}
