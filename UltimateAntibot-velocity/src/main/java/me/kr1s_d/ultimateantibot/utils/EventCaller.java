package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;

public class EventCaller {
    public static void call(Object event) {
        UltimateAntiBotVelocity.getInstance().getServer().getEventManager().fire(event);
    }
}
