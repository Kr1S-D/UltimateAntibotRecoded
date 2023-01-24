package me.kr1s_d.ultimateantibot.common.checks;

public interface Check {
    void onDisconnect(String ip, String name);

    boolean isEnabled();

    void loadTask();
}
