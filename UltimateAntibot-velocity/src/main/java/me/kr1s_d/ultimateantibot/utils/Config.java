package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config implements IConfiguration {
    private final UltimateAntiBotVelocity plugin = UltimateAntiBotVelocity.getInstance();
    private final YamlConfiguration config;

    private File file;
    private final String filePath;

    public Config(String filePath) {
        this.filePath = filePath;
        createConfiguration(filePath);
        this.config = getConfiguration(filePath);
    }

    public YamlConfiguration getConfiguration(String file) {
        file = replaceDataFolder(file);
        try {
            this.file = new File(file);
            return YamlConfiguration.loadConfiguration(this.file);
        } catch (Exception e) {
            return new YamlConfiguration();
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
                this.plugin.log(LogHelper.LogType.INFO, "File " + configFile + " has been created!");
            }
        } catch (IOException e) {
            this.plugin.log(LogHelper.LogType.INFO,"Unable to create configuration file '" + file + "'!");
        }
    }

    public void saveConfiguration(YamlConfiguration configuration, String file) {
        String replacedFile = replaceDataFolder(file);

        try {
            configuration.save(file);
        } catch (IOException e) {
            this.plugin.log(LogHelper.LogType.INFO,"Unable to save configuration file '" + replacedFile + "'!");
        }
    }

    public void deleteConfiguration(String file) {
        String replacedFile = replaceDataFolder(file);
        File file1 = new File(replacedFile);
        if (file1.exists()) {
            file1.delete();
            this.plugin.log(LogHelper.LogType.INFO,"File " + replacedFile + " has been deleted!");
        }
    }

    private String replaceDataFolder(String string) {
        File dataFolder = this.plugin.getDFolder();
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
        config.getConfigurationSection(path).getKeys(false).forEach(p::add);
        return p;
    }

    @Override
    public void set(String path, Object value){
        config.set(path, value);
    }

    @Override
    public void rename(String newName) {
        file.renameTo(new File(plugin.getDFolder(), newName + ".yml"));
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
