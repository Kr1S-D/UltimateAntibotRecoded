package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckReloaded;
import me.kr1s_d.ultimateantibot.checks.PacketCheck;
import me.kr1s_d.ultimateantibot.common.checks.*;
import me.kr1s_d.ultimateantibot.common.checks.slowdetection.AccountCheck;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.tasks.AutoWhitelistTask;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.ComponentBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainEventListener implements Listener {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;
    private final BlackListService blackListService;
    private final FirstJoinCheck firstJoinCheck;
    private final NameChangerCheck nameChangerCheck;
    private final SuperJoinCheck superJoinCheck;
    private final FloodCheck floodCheck;
    private final AuthCheckReloaded authCheck;
    private final PacketCheck packetCheck;
    private final AccountCheck accountCheck;
    private int blacklistedPercentage;
    private final VPNService VPNService;

    public MainEventListener(IAntiBotPlugin antiBotPlugin){
        this.plugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
        this.firstJoinCheck = new FirstJoinCheck(antiBotPlugin);
        this.nameChangerCheck = new NameChangerCheck(antiBotPlugin);
        this.superJoinCheck = new SuperJoinCheck(antiBotPlugin);
        this.floodCheck = new FloodCheck(antiBotPlugin);
        this.authCheck = new AuthCheckReloaded(antiBotPlugin);
        this.packetCheck = new PacketCheck(antiBotPlugin);
        this.accountCheck = new AccountCheck(antiBotPlugin);
        this.blacklistedPercentage = 0;
        this.VPNService = plugin.getVPNService();
    }

    //@EventHandler
    //public void onClientConnection(ClientConnectEvent e){
    //    SocketAddress encodedIP = e.getSocketAddress();
    //    InetSocketAddress socketAddress = (InetSocketAddress) encodedIP;
    //    String ip = socketAddress.getAddress().toString();
    //    e.setCancelled(true);
    //}

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
        if(antiBotManager.getJoinPerSecond() >= ConfigManger.antiBotModeTrigger){
            if(!antiBotManager.isAntiBotModeEnabled()){
                antiBotManager.enableAntiBotMode();
                e.setCancelReason(ComponentBuilder.buildColorized(
                        MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(blacklistedPercentage))
                ));
                e.setCancelled(true);
                return;
            }
        }
        //
        //Queue Service
        //
        if(!queueService.isQueued(ip) && !blackListService.isBlackListed(ip) && !whitelistService.isWhitelisted(ip)){
            queueService.queue(ip);
        }
        //
        //Flood Check
        //
        if (floodCheck.isDenied(ip, name)) {
            blackListService.blacklist(ip, BlackListReason.STRANGE_PLAYER, name);
            e.setCancelReason(blacklistMSG(ip));
            e.setCancelled(true);
            return;
        }
        //
        //FirstJoinCheck
        //
        if(firstJoinCheck.isDenied(ip, name)){
            e.setCancelReason(ComponentBuilder.buildColorized(MessageManager.firstJoinMessage));
            e.setCancelled(true);
            return;
        }
        //
        // NameChangerCheck
        //
        if(nameChangerCheck.isDenied(ip, name)){
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
            e.setCancelReason(blacklistMSG(ip));
            e.setCancelled(true);
            return;
        }
        //
        // SuperJoinCheck
        //
        if(superJoinCheck.isDenied(ip, name)){
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_JOINS, name);
            e.setCancelReason(blacklistMSG(ip));
            e.setCancelled(true);
            return;
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
        if(antiBotManager.isAntiBotModeEnabled() || antiBotManager.isSlowAntiBotModeEnabled()){
            e.setCancelReason(ComponentBuilder.buildColorized(
                    MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(blacklistedPercentage))
                    ));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e){
        ProxiedPlayer player = e.getPlayer();
        String nickname = player.getName();
        String ip = Utils.getIP(player);
        //
        //Packet Check
        //
        packetCheck.registerJoin(ip);
        //
        //Account Check
        //
        if(accountCheck.isDenied(ip, nickname)){
            plugin.disconnect(ip, MessageManager.getAccountOnlineMessage());
            plugin.getLogHelper().debug("Account Check Executed!");
            return;
        }
        //
        //Connection check (ProxyCheck.io or ip-api.com)
        //
        VPNService.submitIP(ip, nickname);
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
        //
        //Packet Check
        //
        packetCheck.onUnLogin(ip);
        //
        //Account Check
        //
        accountCheck.onDisconnect(ip, e.getPlayer().getName());
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