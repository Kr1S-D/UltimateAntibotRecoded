package me.kr1s_d.ultimateantibot.common.checks;

public interface ICheck {
    boolean isDenied(String ip, String name);

    void onDisconnect(String ip, String name);

    boolean isEnabled();

    void loadTask();
}
