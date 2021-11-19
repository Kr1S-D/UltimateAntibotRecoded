package me.kr1s_d.ultimateantibot.common.objects.interfaces;

public interface ICheck {
    void loadTask();
    void check(String string, String name, String uuid);
    void isEnabled();
    void clearData();
}
