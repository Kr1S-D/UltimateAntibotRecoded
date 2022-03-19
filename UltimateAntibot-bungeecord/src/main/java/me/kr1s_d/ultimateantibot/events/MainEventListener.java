package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckReloaded;
import me.kr1s_d.ultimateantibot.checks.PacketCheck;
import me.kr1s_d.ultimateantibot.common.checks.*;
import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.ConnectionCheckerService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
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

public class MainEventListener implements Listener {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;
    private final BlackListService blackListService;
    private final FirstJoinBasicCheck firstJoinCheck;
    private final NameChangerBasicCheck nameChangerCheck;
    private final SuperJoinBasicCheck superJoinCheck;
    private final AuthCheckReloaded authCheck;
    private final PacketCheck packetCheck;
    private final AccountBasicCheck accountCheck;
    private final SimilarNameBasicCheck similarNameCheck;
    private final LengthBasicCheck lengthCheck;
    private int blacklistedPercentage;
    private final ConnectionCheckerService connectionCheckerService;

    public MainEventListener(IAntiBotPlugin antiBotPlugin){
        this.plugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
        this.firstJoinCheck = new FirstJoinBasicCheck(antiBotPlugin);
        this.nameChangerCheck = new NameChangerBasicCheck(antiBotPlugin);
        this.superJoinCheck = new SuperJoinBasicCheck(antiBotPlugin);
        this.authCheck = new AuthCheckReloaded(antiBotPlugin);
        this.packetCheck = new PacketCheck(antiBotPlugin);
        this.accountCheck = new AccountBasicCheck(antiBotPlugin);
        this.similarNameCheck = new SimilarNameBasicCheck(antiBotPlugin);
        this.lengthCheck = new LengthBasicCheck(antiBotPlugin);
        this.blacklistedPercentage = 0;
        this.connectionCheckerService = plugin.getConnectionCheckerService();
    }

    @EventHandler
    public void onPreLoginEvent(PreLoginEvent e){
        //e.registerIntent(UltimateAntiBotBungeeCord.getInstance());
        String ip = Utils.getIP(e.getConnection());
        String name = e.getConnection().getName();
        int totals = blackListService.size() + queueService.size();
        if(blackListService.size() != 0 && totals != 0) {
            blacklistedPercentage = Math.round((float) blackListService.size() / totals * 100);
        }
        //
        //AntiBotInfo Update & Queue clear
        //
        if(whitelistService.isWhitelisted(ip) || blackListService.isBlackListed(ip)){
            queueService.removeQueue(ip);
        }

        if(!whitelistService.isWhitelisted(ip)){
            antiBotManager.increaseJoinPerSecond();
            if(!blackListService.isBlackListed(ip)){
                antiBotManager.increaseChecksPerSecond();
            }
        }
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
        //Queue Service
        //
        if(!queueService.isQueued(ip) && !blackListService.isBlackListed(ip) && !whitelistService.isWhitelisted(ip)){
            queueService.queue(ip);
        }
        //
        //Some Checks
        //
        if(antiBotManager.isAntiBotModeEnabled()) {
            //
            // NameChangerCheck
            //
            if(nameChangerCheck.needToDeny(ip, name)){
                blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
                e.setCancelReason(blacklistMSG(ip));
                e.setCancelled(true);
                return;
            }
            //
            // SuperJoinCheck
            //
            if(superJoinCheck.needToDeny(ip, name)){
                blackListService.blacklist(ip, BlackListReason.TOO_MUCH_JOINS, name);
                e.setCancelReason(blacklistMSG(ip));
                e.setCancelled(true);
                return;
            }
        }
        //
        //FirstJoinCheck
        //
        if(firstJoinCheck.needToDeny(ip, name)){
            e.setCancelReason(ComponentBuilder.buildColorized(MessageManager.firstJoinMessage));
            e.setCancelled(true);
            return;
        }
        //
        //AntiBotMode Enable
        //
        if(antiBotManager.getJoinPerSecond() > ConfigManger.antiBotModeTrigger){
            if(!antiBotManager.isAntiBotModeEnabled()){
                antiBotManager.enableAntiBotMode();
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
        String nickname = player.getName();
        String ip = Utils.getIP(player);
        //
        //Packet Check
        //
        packetCheck.registerJoin(ip);
        //
        //Account Check
        //
        if(accountCheck.needToDeny(ip, nickname)){
            plugin.disconnect(ip, MessageManager.getAccountOnlineMessage());
            plugin.getLogHelper().debug("Account Check Executed!");
            return;
        }
        //
        //Similar Name Check
        //
        if(similarNameCheck.needToDeny(ip, nickname)){
            plugin.disconnect(ip, MessageManager.getSafeModeMessage());
            plugin.getLogHelper().debug("Similar Name Check!");
            return;
        }
        //
        //Length Check
        //
        if(lengthCheck.needToDeny(ip, nickname)){
            plugin.disconnect(ip, MessageManager.getSafeModeMessage());
            plugin.getLogHelper().debug("Length Check Executed!");
            return;
        }
        //
        //Connection check (ProxyCheck.io)
        //
        connectionCheckerService.submit(ip, nickname);
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
        if(player.hasPermission("uab.notification.automatic") && antiBotManager.isAntiBotModeEnabled()){
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
    public void onUnlogin(PlayerDisconnectEvent e){
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
