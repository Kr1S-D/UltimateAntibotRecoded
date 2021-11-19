package me.kr1s_d.ultimateantibot.objects;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class Config implements IConfiguration {
    private final Plugin plugin;

    private final Configuration config;

    private final Logger logger;

    private final TaskScheduler scheduler;

    public Config(Plugin plugin, String file) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.scheduler = plugin.getProxy().getScheduler();
        createConfiguration(file);
        this.config = getConfiguration(file);
    }

    public Configuration getConfiguration(String file) {
        file = replaceDataFolder(file);
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(file));
        } catch (IOException e) {
            return new Configuration();
        }
    }

    public void createConfiguration(String file) {
        try {
            file = replaceDataFolder(file);
            File configFile = new File(file);
            if (!configFile.exists()) {
                String[] files = file.split("/");
                InputStream inputStream = this.plugin.getClass().getClassLoader().getResourceAsStream(files[files.length - 1]);
                File parentFile = configFile.getParentFile();
                if (parentFile != null)
                    parentFile.mkdirs();
                if (inputStream != null) {
                    Files.copy(inputStream, configFile.toPath());
                } else {
                    configFile.createNewFile();
                }
                this.logger.info("File " + configFile + " has been created!");
            }
        } catch (IOException e) {
            this.logger.info("Unable to create configuration file '" + file + "'!");
        }
    }

    public void saveConfiguration(Configuration configuration, String file) {
        String replacedFile = replaceDataFolder(file);
        this.scheduler.runAsync(this.plugin, () -> {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(replacedFile));
            } catch (IOException e) {
                this.logger.info("Unable to save configuration file '" + replacedFile + "'!");
            }
        });
    }

    public void deleteConfiguration(String file) {
        String replacedFile = replaceDataFolder(file);
        File file1 = new File(replacedFile);
        if (file1.exists()) {
            file1.delete();
            this.logger.info("File " + replacedFile + " has been deleted!");
        }
    }

    private String replaceDataFolder(String string) {
        File dataFolder = this.plugin.getDataFolder();
        return string.replace("%datafolder%", dataFolder.toPath().toString());
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
        return (Set<String>) config.getSection(path).getKeys();
    }

    @Override
    public void set(String path, Object value){
        config.set(path, value);
    }
}
