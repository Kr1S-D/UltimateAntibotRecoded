package me.kr1s_d.ultimateantibot.objects;

import me.kr1s_d.ultimateantibot.common.IConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Config implements IConfiguration {
    private final JavaPlugin plugin;
    private final File file;

    private YamlConfiguration config;
    private Consumer<Exception> exceptionHandler;

    private String configName;

    public Config(JavaPlugin plugin, String configName) {
        Player player = null;
        this.plugin = plugin;
        this.configName = configName;
        this.file = new File(plugin.getDataFolder(), configName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(configName + ".yml", false);
        }

        reload();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            if (exceptionHandler == null)
                Bukkit.getLogger().severe(String.format("Unable to save file %s.yml", file.getName()));
            else
                exceptionHandler.accept(e);
        }
    }

    public void setExceptionHandler(Consumer<Exception> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Object get(String path) {
        return config.get(path);
    }

    @Override
    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public int getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public short getShort(String path) {
        return Integer.valueOf(getInt(path)).shortValue();
    }

    @Override
    public long getLong(String path) {
        return config.getLong(path);
    }

    @Override
    public double getDouble(String path) {
        return config.getDouble(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public Set<String> getConfigurationSection(String path) {
        Set<String> p = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection(path);
        if(section == null) return p;
        Collection<String> keys = section.getKeys(false);
        if(keys.isEmpty() || keys == null) return p;
        p.addAll(keys);
        return p;
    }

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public void rename(String newName) {
        file.renameTo(new File(plugin.getDataFolder(), newName + ".yml"));
    }

    @Override
    public void destroy() {
        file.delete();
    }

    public YamlConfiguration asBukkitConfig() {
        return this.config;
    }
}
