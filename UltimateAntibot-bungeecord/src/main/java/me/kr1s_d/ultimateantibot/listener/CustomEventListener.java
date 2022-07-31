package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;
import me.kr1s_d.ultimateantibot.common.detectors.FastJoinBypassDetector;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.event.AttackStateEvent;
import me.kr1s_d.ultimateantibot.event.DuringAttackIPJoinEvent;
import me.kr1s_d.ultimateantibot.event.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.Notificator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CustomEventListener implements Listener {

    private final IAntiBotPlugin plugin;
    private final FastJoinBypassDetector bypassDetector;

    public CustomEventListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.bypassDetector = new FastJoinBypassDetector(plugin);
    }

    @EventHandler
    public void onAttack(ModeEnableEvent e){
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
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
            plugin.getAntiBotManager().getBlackListService().save();
            plugin.getUserDataService().unload();
            plugin.getWhitelist().save();
            ConfigManger.restoreAuthCheck();
        }, true, 1000L);
    }

    @EventHandler
    public void onIPJoinDuringAttack(DuringAttackIPJoinEvent e){
        bypassDetector.registerJoin();
    }
}
