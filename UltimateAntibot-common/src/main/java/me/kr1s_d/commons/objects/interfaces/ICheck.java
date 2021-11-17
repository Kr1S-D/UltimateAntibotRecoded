package me.kr1s_d.commons.objects.interfaces;

public interface ICheck {
    void loadTask();
    void check(String ip, String name);
    void isEnabled();
    void clearData();
}
