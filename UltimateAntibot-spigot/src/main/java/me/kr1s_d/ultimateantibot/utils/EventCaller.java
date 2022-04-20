package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.UltimateAntiBotSpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EventCaller {
    public static void call(Event event){
        Bukkit.getScheduler().runTask(UltimateAntiBotSpigot.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });
    }
}
