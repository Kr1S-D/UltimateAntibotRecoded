package me.kr1s_d.ultimateantibot.common.checks;

public interface IAdvancedCheck {
    boolean needToDeny(String ip, String name);

    boolean isEnabled();

    void loadTask();

    void registerPing(String ip);
}
