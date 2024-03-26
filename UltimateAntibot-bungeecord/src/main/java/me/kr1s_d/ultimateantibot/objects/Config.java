package me.kr1s_d.ultimateantibot.objects;

import me.kr1s_d.ultimateantibot.common.IConfiguration;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class Config implements IConfiguration {
    private final Plugin plugin;
    private final Configuration config;
    private final Logger logger;

    private File file;
    private final String filePath;

    public Config(Plugin plugin, String filePath) {
        this.filePath = filePath;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        createConfiguration(filePath);
        this.config = getConfiguration(filePath);
    }

    public Configuration getConfiguration(String file) {
        file = replaceDataFolder(file);
        try {
            this.file = new File(file);
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
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
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, new File(replacedFile));
        } catch (IOException e) {
            this.logger.info("Unable to save configuration file '" + replacedFile + "'!");
        }
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
        Set<String> p = new HashSet<>();
        Configuration section = config.getSection(path);
        if(section == null) return p;
        Collection<String> keys = section.getKeys();
        if(keys.isEmpty() || keys == null) return p;
        p.addAll(keys);
        return p;
    }

    @Override
    public void set(String path, Object value){
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

    @Override
    public void save(){
        saveConfiguration(config, filePath);
    }
}
