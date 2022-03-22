package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.events.custom.ModeEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {
    @EventHandler
    public void onAttack(ModeEnableEvent e){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.hasPermission("uab.notification.automatic")){
                Notificator.automaticNotification(player);
            }
        }
    }
}
