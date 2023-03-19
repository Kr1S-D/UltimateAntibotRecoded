package me.kr1s_d.ultimateantibot.common.checks;

public interface Check {
    CheckType getType();

    void onDisconnect(String ip, String name);

    boolean isEnabled();

    long getCacheSize();

    void clearCache();

    void removeCache(String ip);
}
