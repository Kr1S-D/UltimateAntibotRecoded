package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.events.custom.AttackStateEvent;
import me.kr1s_d.ultimateantibot.events.custom.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.Notificator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CustomEventListener implements Listener {

    private final IAntiBotPlugin plugin;

    public CustomEventListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(ModeEnableEvent e){
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
            if(player.hasPermission("uab.notification.automatic")){
                Notificator.automaticNotification(player);
            }
        }
    }

    @EventHandler
    public void onAttackStop(AttackStateEvent e){
        if(e.getAttackState() != AttackState.STOPPED){
            return;
        }

        plugin.scheduleDelayedTask(() -> {
            plugin.getAntiBotManager().getBlackListService().save();
            plugin.getUserDataService().save();
            plugin.getWhitelist().save();
        }, false, 1000L);
    }
}
