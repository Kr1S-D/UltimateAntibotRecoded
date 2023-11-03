package me.kr1s_d.ultimateantibot.event;

public class DuringAttackIPJoinEvent {
    private final String ip;

    public DuringAttackIPJoinEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
