package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IConfiguration;

public class FilesUpdater {
    public static boolean checkFiles(IAntiBotPlugin pl, double fileVersion, IConfiguration config, IConfiguration messages){
        double configVersion = config.getDouble("version");
        double messageVersion = messages.getDouble("version");
        if(configVersion != fileVersion) {
            pl.scheduleRepeatingTask(() -> {
                pl.getLogHelper().error("Your config.yml is outdated! Rebuild it to make the plugin work properly!");
            }, false, 100);
            return true;
        }
        if(messageVersion != fileVersion){
            pl.scheduleRepeatingTask(() -> {
                pl.getLogHelper().error("Your message.yml is outdated! Rebuild it to make the plugin work properly!");
            }, false, 100);
            return true;
        }
        return false;
    }

}
