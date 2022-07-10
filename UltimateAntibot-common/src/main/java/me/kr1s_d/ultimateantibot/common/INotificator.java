package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

public interface INotificator {
    void sendActionbar(String str);

    void sendTitle(String title, String subtitle);

    void sendBossBarMessage(String str, float health);

    void init(IAntiBotPlugin plugin);

}
