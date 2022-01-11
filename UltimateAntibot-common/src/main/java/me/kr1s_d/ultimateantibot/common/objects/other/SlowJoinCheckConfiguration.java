package me.kr1s_d.ultimateantibot.common.objects.other;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

public class SlowJoinCheckConfiguration {
    private final int time;
    private final int trigger;
    private final int condition;
    private final boolean blacklist;
    private final boolean kick;
    private final boolean enableAntiBotMode;
    private final boolean isEnabled;

    public SlowJoinCheckConfiguration(IConfiguration configuration, String path){
        time = configuration.getInt(path + ".time");
        trigger = configuration.getInt(path + ".trigger");
        condition = configuration.getInt(path + ".condition");
        blacklist = configuration.getBoolean(path + ".blacklist");
        kick = configuration.getBoolean(path +  ".kick");
        enableAntiBotMode = configuration.getBoolean(path + ".antibotmode");
        isEnabled = configuration.getBoolean(path + ".enabled");
    }

    public int getTime() {
        return time;
    }

    public int getTrigger() {
        return trigger;
    }

    public int getCondition() {
        return condition;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public boolean isKick() {
        return kick;
    }

    public boolean isEnableAntiBotMode() {
        return enableAntiBotMode;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
