package net.norius.voteSystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.norius.voteSystem.VoteSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ConfigLoader {

    private FileConfiguration lang;
    private Component prefix;

    private final VoteSystem plugin;

    public ConfigLoader(VoteSystem plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File langFile = new File(plugin.getDataFolder(), "lang.yml");

        if(!langFile.exists()) {
            plugin.saveResource("lang.yml", true);
        }

        this.lang = YamlConfiguration.loadConfiguration(langFile);
        this.prefix = MiniMessage.miniMessage().deserialize(Objects.requireNonNull(plugin.getConfig().getString("prefix")));
    }

    public Component get(String path, boolean usePrefix) {
        return usePrefix && plugin.getConfig().getBoolean("settings.use-prefix") ? prefix.append(Component.text(" ")).append(
                MiniMessage.miniMessage().deserialize(Objects.requireNonNull(lang.getString(path)))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)) :
                MiniMessage.miniMessage().deserialize(Objects.requireNonNull(lang.getString(path)))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public Component get(String path, List<String> keys, List<Component> values, boolean usePrefix) {
        return usePrefix && plugin.getConfig().getBoolean("settings.use-prefix") ? prefix.append(Component.text(" ")).append(
                replace(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(lang.getString(path)))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE), keys, values)
                        .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)) :
                replace(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(lang.getString(path)))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE), keys, values);
    }

    public List<Component> getList(String path, List<String> keys, List<Component> values) {
        return this.lang.getStringList(path).stream().map(s -> replace(MiniMessage.miniMessage().deserialize(s)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE), keys, values)).toList();
    }

    private Component replace(Component value, List<String> keys, List<Component> values) {
        for(int i = 0; i < keys.size(); i++) {
            int finalI = i;
            value = value.replaceText(builder -> builder.match("%" + keys.get(finalI) + "%").replacement(values.get(finalI))).
                    replaceText(builder -> builder.match("%prefix%").replacement(prefix));
        }

        return value;
    }

    public Component getPrefix() {
        return prefix;
    }

    public String getString(String path) {
        return lang.getString(path);
    }
}
