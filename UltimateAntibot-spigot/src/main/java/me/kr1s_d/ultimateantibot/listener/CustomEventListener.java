package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.detectors.FastJoinBypassDetector;
import me.kr1s_d.ultimateantibot.event.AttackStateEvent;
import me.kr1s_d.ultimateantibot.event.DuringAttackIPJoinEvent;
import me.kr1s_d.ultimateantibot.event.ModeEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

    private final IAntiBotPlugin plugin;
    private final FastJoinBypassDetector bypassDetector;

    public CustomEventListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.bypassDetector = new FastJoinBypassDetector(plugin);
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
        }, true, 1000L);
    }

    @EventHandler
    public void onIPJoinDuringAttack(DuringAttackIPJoinEvent e){
        bypassDetector.registerJoin();
    }
}
