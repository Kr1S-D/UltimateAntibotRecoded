package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckReloaded;
import me.kr1s_d.ultimateantibot.checks.PacketCheck;
import me.kr1s_d.ultimateantibot.common.checks.*;
import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.enums.CheckListenedEvent;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IManagedCheck;
import me.kr1s_d.ultimateantibot.common.service.*;
import me.kr1s_d.ultimateantibot.common.tasks.AutoWhitelistTask;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.ComponentBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainEventListenerV2 implements Listener {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;
    private final BlackListService blackListService;
    private final CheckService checkService;
    private final AuthCheckReloaded authCheck;
    private final PacketCheck packetCheck;
    private int blacklistedPercentage;
    private final ConnectionCheckerService connectionCheckerService;

    public MainEventListenerV2(IAntiBotPlugin antiBotPlugin){
        this.plugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
        this.checkService = plugin.getCheckService();
        this.authCheck = new AuthCheckReloaded(antiBotPlugin);
        this.packetCheck = new PacketCheck(antiBotPlugin);
        this.blacklistedPercentage = 0;
        this.connectionCheckerService = plugin.getConnectionCheckerService();
    }

    @EventHandler(priority = -128)
    public void onPreLoginEvent(PreLoginEvent e){
        //e.registerIntent(UltimateAntiBotBungeeCord.getInstance());
        String ip = Utils.getIP(e.getConnection());
        String name = e.getConnection().getName();
        int totals = blackListService.size() + queueService.size();
        if(blackListService.size() != 0 && totals != 0) {
            blacklistedPercentage = Math.round((float) blackListService.size() / totals * 100);
        }
        antiBotManager.increaseJoinPerSecond();

        //
        //BlackList & Whitelist Checks
        //
        if(blackListService.isBlackListed(ip)){
            e.setCancelReason(blacklistMSG(ip));
            e.setCancelled(true);
            return;
        }
        if(whitelistService.isWhitelisted(ip)){
            return;
        }
        //
        //AntiBotMode Enable
        //
        if(antiBotManager.getJoinPerSecond() > ConfigManger.antiBotModeTrigger){
            if(!antiBotManager.isAntiBotModeEnabled()){
                antiBotManager.enableAntiBotMode();
                return;
            }
        }
        //
        //Check Execution Rate
        //
        for(IManagedCheck check : checkService.getCheckListByEvent(CheckListenedEvent.PRELOGIN)){
            if(check.requireAntiBotMode()){
                //plugin.getLogHelper().info("Running check " + check.getCheckName());
                if(check.isDenied(ip, name)){
                    check.onDisconnect(ip, name);
                    e.setCancelReason("a");
                    e.setCancelled(true);
                    return;
                }
            }
            //plugin.getLogHelper().info("Executing check " + check.getCheckName());
            if(check.isDenied(ip, name)){
                check.onDisconnect(ip, name);
                e.setCancelReason("a");
                e.setCancelled(true);
                return;
            }
        }
        //
        //Auth Check
        //
        if(blacklistedPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled()){
            authCheck.onJoin(e, ip);
            return;
        }
        //
        //AntiBotMode Normal
        //
        if(antiBotManager.isAntiBotModeEnabled()){
            e.setCancelReason(ComponentBuilder.buildColorized(
                    MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(blacklistedPercentage))
            ));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e){
        ProxiedPlayer player = e.getPlayer();
        String name = player.getName();
        String ip = Utils.getIP(player);
        //
        //Packet Check
        //
        packetCheck.registerJoin(ip);
        //
        //Check Execution Rate
        //
        for(IManagedCheck check : checkService.getCheckListByEvent(CheckListenedEvent.POSTLOGIN)){
            if(check.isDenied(ip, name)){
                check.onDisconnect(ip, name);
                return;
            }
        }
        //
        //Connection check (ProxyCheck.io)
        //
        connectionCheckerService.submit(ip, name);
        //If isn't whitelisted
        if(!antiBotManager.getWhitelistService().isWhitelisted(ip)){
            //Add to last join
            antiBotManager.getJoinCache().addJoined(ip);
            //Auto Whitelist Task
            plugin.scheduleDelayedTask(new AutoWhitelistTask(plugin, ip), false, 1000L * ConfigManger.playtimeForWhitelist * 60L);
            //Remove from JoinCache after 30 Seconds
            plugin.scheduleDelayedTask(() -> antiBotManager.getJoinCache().removeJoined(ip),false,1000L * 30);
        }
        //Notification
        if(player.hasPermission("uab.notification.automatic") && antiBotManager.isSomeModeOnline()){
            Notificator.automaticNotification(player);
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent e){
        String ip = Utils.getIP(e.getConnection());
        //
        //Auth Check Ping Action
        //
        if(blacklistedPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled()) {
            authCheck.onPing(e, ip);
        }
    }

    @EventHandler
    public void onUnLogin(PlayerDisconnectEvent e){
        String ip = Utils.getIP(e.getPlayer());
        String name = e.getPlayer().getName();
        //
        //Packet Check
        //
        packetCheck.onUnLogin(ip);
        //
        //Disconnect Rate
        //
        checkService.executeDisconnect(ip, name);
    }

    @EventHandler
    public void onSettings(SettingsChangedEvent e){
        String ip = Utils.getIP(e.getPlayer());
        //
        //PacketCheck
        //
        packetCheck.registerPacket(ip);
    }

    private BaseComponent blacklistMSG(String ip){
        return ComponentBuilder.buildColorized(MessageManager.getBlacklistedMessage(blackListService.getProfile(ip)));
    }

}
