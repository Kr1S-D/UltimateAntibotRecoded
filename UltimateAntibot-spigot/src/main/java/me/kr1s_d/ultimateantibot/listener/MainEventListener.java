package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckReloaded;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.impl.*;
import me.kr1s_d.ultimateantibot.common.core.tasks.AutoWhitelistTask;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.*;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.component.KComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
    private final AuthCheckReloaded authCheck;
    private final AccountCheck accountCheck;
    private final SlowJoinCheck slowJoinCheck;
    private final LegalNameCheck legalNameCheck;
    private final InvalidNameCheck invalidNameCheck;
    private final RegisterCheck registerCheck;
    private final VPNService VPNService;
    private final UserDataService userDataService;

    public MainEventListener(IAntiBotPlugin antiBotPlugin) {
        this.plugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
        this.firstJoinCheck = new FirstJoinCheck(antiBotPlugin);
        this.nameChangerCheck = new NameChangerCheck(antiBotPlugin);
        this.superJoinCheck = new SuperJoinCheck(antiBotPlugin);
        this.authCheck = new AuthCheckReloaded(antiBotPlugin);
        this.accountCheck = new AccountCheck(antiBotPlugin);
        this.slowJoinCheck = new SlowJoinCheck(antiBotPlugin);
        this.legalNameCheck = new LegalNameCheck(antiBotPlugin);
        this.invalidNameCheck = new InvalidNameCheck(plugin);
        this.registerCheck = new RegisterCheck(plugin);
        this.VPNService = plugin.getVPNService();
        this.userDataService = plugin.getUserDataService();
        new ConnectionAnalyzerCheck(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLoginEvent(AsyncPlayerPreLoginEvent e) {
        //e.registerIntent(UltimateAntiBotBungeeCord.getInstance());
        String ip = Utils.getInetAddressIP(e.getAddress());
        String name = e.getName();
        int totals = blackListService.size() + queueService.size();
        if (blackListService.size() != 0 && totals != 0) {
            ServerUtil.blacklistPercentage = Math.round((float) blackListService.size() / totals * 100);
        }
        antiBotManager.increaseJoinPerSecond();

        //anti crash on attack start for first 5s
        if((System.currentTimeMillis() - ServerUtil.lastStartAttack) < 5000) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.fastJoinQueueMessage));
            return;
        }

        //
        //BlackList & Whitelist Checks
        //
        if (blackListService.isBlackListed(ip)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        if (whitelistService.isWhitelisted(ip)) {
            antiBotManager.getDynamicJoins().decrease();
            return;
        }
        //
        //AntiBotMode Enable
        //
        if (antiBotManager.getSpeedJoinPerSecond() >= ConfigManger.antiBotModeTrigger) {
            if (!antiBotManager.isAntiBotModeEnabled()) {
                antiBotManager.enableAntiBotMode();
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(ServerUtil.blacklistPercentage))));
                return;
            }
        }
        //
        // NameChangerCheck
        //
        if (nameChangerCheck.isDenied(ip, name)) {
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        //
        //Queue Check
        //
        if (!queueService.isQueued(ip) && !blackListService.isBlackListed(ip) && !whitelistService.isWhitelisted(ip)) {
            queueService.queue(ip);
        }
        //
        //Legal Name Check
        //
        if (legalNameCheck.isDenied(ip, name)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        //
        // SuperJoinCheck
        //
        if (superJoinCheck.isDenied(ip, name)) {
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_JOINS, name);
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        //
        // Invalid Name Check
        //
        if (invalidNameCheck.isDenied(ip, name)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, blacklistMSG(ip));
            return;
        }
        //
        //Auth Check
        //
        if (ServerUtil.blacklistPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled()) {
            authCheck.onJoin(e, ip);
            return;
        }
        //
        //AntiBotMode Normal
        //
        if (antiBotManager.isAntiBotModeEnabled() || antiBotManager.isSlowAntiBotModeEnabled()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(ServerUtil.blacklistPercentage))));
            return;
        }

        //
        //FirstJoinCheck
        //
        if (firstJoinCheck.isDenied(ip, name)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.colora(MessageManager.firstJoinMessage));
            return;
        }
    }

    @EventHandler
    public void onPostLoginEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String nickname = player.getName();
        String ip = Utils.getPlayerIP(player);
        //
        //connection profiles
        //
        userDataService.registerJoin(ip, nickname);
        //
        //Account Check
        //
        if (accountCheck.isDenied(ip, nickname)) {
            plugin.disconnect(ip, Utils.colora(MessageManager.getAccountOnlineMessage()));
            return;
        }
        //
        //SlowJoin check
        //
        if (slowJoinCheck.isDenied(ip, nickname)) {
            blackListService.blacklist(ip, BlackListReason.STRANGE_PLAYER_SLOW_JOIN, nickname);
            plugin.disconnect(ip, MessageManager.getSafeModeMessage());
            return;
        }
        //If isn't whitelisted
        if (!antiBotManager.getWhitelistService().isWhitelisted(ip)) {
            //Add to last join
            antiBotManager.getJoinCache().addJoined(ip);
            //Auto Whitelist Task
            plugin.scheduleDelayedTask(new AutoWhitelistTask(plugin, ip), false, 1000L * ConfigManger.playtimeForWhitelist * 60L);
            //Remove from JoinCache after 30 Seconds (committed since cache added auto removal in JoinCache)
            //plugin.scheduleDelayedTask(() -> antiBotManager.getJoinCache().removeJoined(ip), false, 1000L * 30);
            //
            //Connection check (ProxyCheck.io or ip-api.com)
            //
            if (!player.hasPermission("uab.bypass.vpn")) {
                VPNService.submitIP(ip, nickname);
            }
        }
        //Notification
        if (player.hasPermission("uab.notification.automatic") && antiBotManager.isAntiBotModeEnabled()) {
            Notificator.automaticNotification(player);
        }
    }

    @EventHandler
        public void onChat(PlayerCommandPreprocessEvent e) {
        String ip = Utils.getPlayerIP(e.getPlayer());
        String nickname = e.getPlayer().getName();

        //
        //Register Check
        //
        registerCheck.onChat(ip, nickname, e.getMessage());
        //
        // Connection Analyze
        //
        CheckService.getCheck(CheckType.CONNECTION_ANALYZE, ConnectionAnalyzerCheck.class).onChat(ip, nickname, e.getMessage());
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        String ip = Utils.getInetAddressIP(e.getAddress());
        //
        //Auth Check Ping Action
        //
        if (ServerUtil.blacklistPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled()) {
            authCheck.onPing(e, ip);
        }
        //
        //Connection Analyze
        //
        CheckService.getCheck(CheckType.CONNECTION_ANALYZE, ConnectionAnalyzerCheck.class).onPing(ip);
    }

    @EventHandler
    public void onUnLogin(PlayerQuitEvent e) {
        String ip = Utils.getPlayerIP(e.getPlayer());
        Notificator.onQuit(e.getPlayer());
        //
        //Account Check
        //
        accountCheck.onDisconnect(ip, e.getPlayer().getName());
        //
        //connection profiles
        //
        userDataService.registerQuit(ip);
        //
        //Invalid name check
        //
        invalidNameCheck.onDisconnect(ip, e.getPlayer().getName());
        //
        //Register check
        //
        registerCheck.onDisconnect(ip, e.getPlayer().getName());
    }

    private String blacklistMSG(String ip) {
        return Utils.colora(MessageManager.getBlacklistedMessage(blackListService.getProfile(ip)));
    }
}
