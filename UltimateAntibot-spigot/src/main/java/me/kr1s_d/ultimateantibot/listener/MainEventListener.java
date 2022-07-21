package me.kr1s_d.ultimateantibot.listener;

import com.sun.scenario.effect.Flood;
import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckReloaded;
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
    private final FirstJoinCheck firstJoinCheck;
    private final NameChangerCheck nameChangerCheck;
    private final SuperJoinCheck superJoinCheck;
    private final FloodCheck floodCheck;
    private final AuthCheckReloaded authCheck;
    private final AccountCheck accountCheck;
    private final LegalNameCheck legalNameCheck;
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
        this.accountCheck = new AccountCheck(antiBotPlugin);
        this.legalNameCheck = new LegalNameCheck(antiBotPlugin);
        this.blacklistedPercentage = 0;
        this.VPNService = plugin.getVPNService();
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
        if(antiBotManager.getJoinPerSecond() >= ConfigManger.antiBotModeTrigger){
            if(!antiBotManager.isAntiBotModeEnabled()){
                antiBotManager.enableAntiBotMode();
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(blacklistedPercentage))));
                return;
            }
        }
        //
        //Queue Check
        //
        if(whitelistService.isWhitelisted(ip) || blackListService.isBlackListed(ip)){
            queueService.removeQueue(ip);
        }
        //
        //Legal Name Check
        //
        if(legalNameCheck.isDenied(ip, name)){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        //
        //Flood Check
        //
        if (floodCheck.isDenied(ip, name)) {
            blackListService.blacklist(ip, BlackListReason.STRANGE_PLAYER, name);
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        //
        //FirstJoinCheck
        //
        if(firstJoinCheck.isDenied(ip, name)){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.firstJoinMessage));
            return;
        }
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
        //Connection check (ProxyCheck.io)
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
