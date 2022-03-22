package me.kr1s_d.ultimateantibot.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EventCaller {
    public static void call(Event event){
        Bukkit.getPluginManager().callEvent(event);
    }
}
