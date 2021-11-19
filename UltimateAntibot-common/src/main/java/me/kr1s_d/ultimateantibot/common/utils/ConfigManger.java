package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

public class ConfigManger {
    public static double version;

    public static void init(IConfiguration configuration){
        version = configuration.getDouble("version");
    }
}
