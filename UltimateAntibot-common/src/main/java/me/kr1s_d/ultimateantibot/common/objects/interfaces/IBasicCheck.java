package me.kr1s_d.ultimateantibot.common.objects.interfaces;

public interface IBasicCheck {
    boolean needToDeny(String ip, String name);

    boolean isEnabled();

    void loadTask();

}
