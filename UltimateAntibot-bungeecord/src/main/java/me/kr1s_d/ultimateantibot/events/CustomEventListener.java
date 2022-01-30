package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.events.custom.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.Notificator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CustomEventListener implements Listener {

    @EventHandler
    public void onAttack(ModeEnableEvent e){
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
            if(player.hasPermission("uab.notification.automatic")){
                Notificator.automaticNotification(player);
            }
        }
    }
}
