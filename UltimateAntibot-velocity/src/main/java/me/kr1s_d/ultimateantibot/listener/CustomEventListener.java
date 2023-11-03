package me.kr1s_d.ultimateantibot.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;
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

public class CustomEventListener {
    private final IAntiBotPlugin plugin;
    private final FastJoinBypassDetector bypassDetector;

    private final AttackTrackerService trackerService;

    public CustomEventListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.bypassDetector = new FastJoinBypassDetector(plugin);
        this.trackerService = plugin.getAttackTrackerService();
    }

    @Subscribe
    public void onAttack(ModeEnableEvent e){
        for(Player player : UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers()){
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

    @Subscribe
    public void onAttackStop(AttackStateEvent e){
        if(e.getAttackState() != AttackState.STOPPED) {
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

    @Subscribe
    public void onAttackStart(ModeEnableEvent e) {
        if(e.getEnabledMode() == ModeType.OFFLINE) return;

        trackerService.onNewAttackStart();
    }

    @Subscribe
    public void onIPJoinDuringAttack(DuringAttackIPJoinEvent e){
        bypassDetector.registerJoin();
    }
}
