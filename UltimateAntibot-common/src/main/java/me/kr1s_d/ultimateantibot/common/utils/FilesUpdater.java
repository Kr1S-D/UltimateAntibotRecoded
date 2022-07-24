package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IConfiguration;

public class FilesUpdater {
    public static boolean isValid(double fileVersion, IConfiguration config, IConfiguration messages){
        double configVersion = config.getDouble("version");
        double messageVersion = messages.getDouble("version");

        return configVersion == fileVersion && messageVersion == fileVersion;
    }

}
