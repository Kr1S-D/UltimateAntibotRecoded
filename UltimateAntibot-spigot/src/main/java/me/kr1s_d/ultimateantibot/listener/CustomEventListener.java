package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;
import me.kr1s_d.ultimateantibot.common.core.detectors.FastJoinBypassDetector;
import me.kr1s_d.ultimateantibot.common.service.AttackTrackerService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
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
    private final AttackTrackerService trackerService;

    public CustomEventListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.bypassDetector = new FastJoinBypassDetector(plugin);
        this.trackerService = plugin.getAttackTrackerService();
    }

    @EventHandler
    public void onAttack(ModeEnableEvent e){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.hasPermission("uab.notification.automatic")){
                Notificator.automaticNotification(player);
            }
        }

        if(e.getEnabledMode().equals(ModeType.ANTIBOT) || e.getEnabledMode().equals(ModeType.SLOW)){
            if(ConfigManger.antibotDisconnect) {
                e.disconnectBots();
            }
        }
    }

    @EventHandler
    public void onAttackStop(AttackStateEvent e){
        if(e.getAttackState() != AttackState.STOPPED){
            return;
        }

        plugin.scheduleDelayedTask(() -> {
            if(plugin.getAntiBotManager().isSomeModeOnline()) return;
            if(ConfigManger.disableNotificationAfterAttack){
                Notificator.disableAllNotifications();
            }

            trackerService.onAttackStop();
            ServerUtil.setLastAttack(System.currentTimeMillis());

            plugin.scheduleDelayedTask(() -> {
                if(plugin.getAntiBotManager().isSomeModeOnline()) return;
                plugin.getAntiBotManager().getBlackListService().save();
                plugin.getUserDataService().unload();
                plugin.getWhitelist().save();
            }, false, 1000L);
        }, false, 1000L * 3);
    }

    @EventHandler
    public void onAttackStart(ModeEnableEvent e) {
        if(e.getEnabledMode() == ModeType.OFFLINE) {
            return;
        }

        trackerService.onNewAttackStart();
    }

    @EventHandler
    public void onIPJoinDuringAttack(DuringAttackIPJoinEvent e){
        bypassDetector.registerJoin();
    }
}
