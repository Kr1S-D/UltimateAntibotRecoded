package me.kr1s_d.ultimateantibot.common.objects.interfaces;

public interface ICheck {
    boolean needToDeny(String ip, String name, String uuid);

    boolean isEnabled();

    void loadTask();

}
