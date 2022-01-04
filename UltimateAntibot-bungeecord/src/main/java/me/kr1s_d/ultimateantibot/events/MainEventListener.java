package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.checks.AuthCheck;
import me.kr1s_d.ultimateantibot.common.checks.FirstJoinCheck;
import me.kr1s_d.ultimateantibot.common.checks.NameChangerCheck;
import me.kr1s_d.ultimateantibot.common.checks.SuperJoinCheck;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.other.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.tasks.AutoWhitelistTask;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.ComponentBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
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
    private final AuthCheck authCheck;

    public MainEventListener(IAntiBotPlugin antiBotPlugin){
        this.plugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
        this.firstJoinCheck = new FirstJoinCheck(antiBotPlugin);
        this.nameChangerCheck = new NameChangerCheck(antiBotPlugin);
        this.superJoinCheck = new SuperJoinCheck(antiBotPlugin);
        this.authCheck = new AuthCheck(antiBotPlugin);
    }

    @EventHandler(priority = -128)
    public void onPreLoginEvent(PreLoginEvent e){
        String ip = Utils.getIP(e.getConnection());
        String name = e.getConnection().getName();
        int blacklistedPercentage = 0;
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
        if(antiBotManager.isAntiBotModeEnabled()){
            antiBotManager.increaseTotalBots();
        }
        //
        //BlackList & Whitelist Checks
        //
        if(whitelistService.isWhitelisted(ip)){
            return;
        }
        if(blackListService.isBlackListed(ip)){
            whitelistService.unWhitelist(ip);
            e.setCancelReason(blacklistMSG(ip));
            e.setCancelled(true);
            return;
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
                blackListService.blacklist(ip, MessageManager.reasonTooManyNicks);
                e.setCancelReason(blacklistMSG(ip));
                e.setCancelled(true);
                return;
            }
            //
            // SuperJoinCheck
            //
            if(superJoinCheck.needToDeny(ip, name)){
                blackListService.blacklist(ip, MessageManager.reasonTooManyJoins);
                e.setCancelReason(blacklistMSG(ip));
                e.setCancelled(true);
                return;
            }
        }
        //
        //AntiBotMode Enable
        //
        if(antiBotManager.getJoinPerSecond() > ConfigManger.antiBotModeTrigger){
            if(!antiBotManager.isAntiBotModeEnabled()){
                Utils.debug("a");
                antiBotManager.enableAntiBotMode();
            }
        }
        //
        //Auth Check
        //
        if(blacklistedPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled() && ConfigManger.authEnabled){
            authCheck.checkForJoin(e, ip);
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
    public void onLoginEvent(PostLoginEvent e){
        ProxiedPlayer player = e.getPlayer();
        String ip = Utils.getIP(player);
        if(!antiBotManager.getWhitelistService().isWhitelisted(ip)){
            antiBotManager.getJoinCache().addJoined(ip);
            plugin.scheduleDelayedTask(new AutoWhitelistTask(plugin, ip), false, 1000L * ConfigManger.playtimeForWhitelist * 60L);
            plugin.scheduleDelayedTask(() -> antiBotManager.getJoinCache().removeJoined(ip),false,1000L * 30);
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent e){
        String ip = Utils.getIP(e.getConnection());
        authCheck.onPing(e, ip);
    }

    @EventHandler
    public void onUnlogin(PlayerDisconnectEvent e){
        String ip = Utils.getIP(e.getPlayer());
    }

    private BaseComponent blacklistMSG(String ip){
        return ComponentBuilder.buildColorized(MessageManager.getBlacklistedMessage(blackListService.getProfile(ip)));
    }

}
