package me.kr1s_d.ultimateantibot.event;

import net.md_5.bungee.api.plugin.Event;

public class DuringAttackIPJoinEvent extends Event {
    private final String ip;

    public DuringAttackIPJoinEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
