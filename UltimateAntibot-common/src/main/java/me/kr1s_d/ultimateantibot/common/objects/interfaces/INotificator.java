package me.kr1s_d.ultimateantibot.common.objects.interfaces;

public interface INotificator {
    void sendActionbar(String str);

    void sendTitle(String title, String subtitle);

    void sendBossBarMessage(String str, float health);

    void init(IAntiBotPlugin plugin);

}
