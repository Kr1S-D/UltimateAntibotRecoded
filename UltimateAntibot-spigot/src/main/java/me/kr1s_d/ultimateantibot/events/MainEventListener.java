package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckReloaded;
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
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

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
        this.accountCheck = new AccountBasicCheck(antiBotPlugin);
        this.similarNameCheck = new SimilarNameBasicCheck(antiBotPlugin);
        this.lengthCheck = new LengthBasicCheck(antiBotPlugin);
        this.blacklistedPercentage = 0;
        this.connectionCheckerService = plugin.getConnectionCheckerService();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLoginEvent(AsyncPlayerPreLoginEvent e){
        //e.registerIntent(UltimateAntiBotBungeeCord.getInstance());
        String ip = Utils.getInetAddressIP(e.getAddress());
        String name = e.getName();
        int totals = blackListService.size() + queueService.size();
        if(blackListService.size() != 0 && totals != 0) {
            blacklistedPercentage = Math.round((float) blackListService.size() / totals * 100);
        }
        antiBotManager.increaseJoinPerSecond();

        //
        //BlackList & Whitelist Checks
        //
        if(blackListService.isBlackListed(ip)){
            antiBotManager.increaseChecksPerSecond();
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
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
            if(nameChangerCheck.isDenied(ip, name)){
                blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
                return;
            }
            //
            // SuperJoinCheck
            //
            if(superJoinCheck.isDenied(ip, name)){
                blackListService.blacklist(ip, BlackListReason.TOO_MUCH_JOINS, name);
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
                return;
            }
        }
        //
        //FirstJoinCheck
        //
        if(firstJoinCheck.isDenied(ip, name)){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.firstJoinMessage));
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
        if(antiBotManager.isAntiBotModeEnabled()){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(blacklistedPercentage))));
        }
    }

    @EventHandler
    public void onPostLoginEvent(PlayerJoinEvent e){
        Player player = e.getPlayer();
        String nickname = player.getName();
        String ip = Utils.getPlayerIP(player);
        //
        //Account Check
        //
        if(accountCheck.isDenied(ip, nickname)){
            plugin.disconnect(ip, Utils.colora(MessageManager.getAccountOnlineMessage()));
            plugin.getLogHelper().debug("Account Check Executed!");
            return;
        }
        //
        //Similar Name Check
        //
        if(similarNameCheck.isDenied(ip, nickname)){
            plugin.disconnect(ip, Utils.colora(MessageManager.getSafeModeMessage()));
            plugin.getLogHelper().debug("Similar Name Check!");
            return;
        }
        //
        //Length Check
        //
        if(lengthCheck.isDenied(ip, nickname)){
            plugin.disconnect(ip, Utils.colora(MessageManager.getSafeModeMessage()));
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
    public void onPing(ServerListPingEvent e){
        String ip = Utils.getInetAddressIP(e.getAddress());
        //
        //Auth Check Ping Action
        //
        if(blacklistedPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled()) {
            authCheck.onPing(e, ip);
        }
    }

    @EventHandler
    public void onUnLogin(PlayerQuitEvent e){
        String ip = Utils.getPlayerIP(e.getPlayer());
        //
        //Account Check
        //
        accountCheck.onDisconnect(ip, e.getPlayer().getName());
    }

    private String blacklistMSG(String ip){
        return Utils.colora(MessageManager.getBlacklistedMessage(blackListService.getProfile(ip)));
    }

}
