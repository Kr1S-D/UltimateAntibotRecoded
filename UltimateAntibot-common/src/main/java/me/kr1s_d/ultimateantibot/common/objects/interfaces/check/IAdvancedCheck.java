package me.kr1s_d.ultimateantibot.common.objects.interfaces.check;

public interface IAdvancedCheck {
    boolean needToDeny(String ip, String name);

    boolean isEnabled();

    void loadTask();

    void registerPing(String ip);
}
