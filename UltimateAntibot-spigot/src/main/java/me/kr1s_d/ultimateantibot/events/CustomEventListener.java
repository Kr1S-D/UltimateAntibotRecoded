package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.events.custom.AttackStateEvent;
import me.kr1s_d.ultimateantibot.events.custom.ModeEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

    private final IAntiBotPlugin plugin;

    public CustomEventListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(ModeEnableEvent e){
        for(Player player : Bukkit.getOnlinePlayers()){
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
