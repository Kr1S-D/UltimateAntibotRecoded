package me.kr1s_d.ultimateantibot.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuringAttackIPJoinEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final String ip;

    public DuringAttackIPJoinEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
