package me.kr1s_d.ultimateantibot.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerSettingsChangedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.checks.AuthCheckVelocity;
import me.kr1s_d.ultimateantibot.checks.PacketCheckVelocity;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.impl.*;
import me.kr1s_d.ultimateantibot.common.core.tasks.AutoWhitelistTask;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.component.KComponentBuilder;
import net.kyori.adventure.text.Component;

public class MainEventListener {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;
    private final BlackListService blackListService;
    private final FirstJoinCheck firstJoinCheck;
    private final NameChangerCheck nameChangerCheck;
    private final SuperJoinCheck superJoinCheck;
    private final AuthCheckVelocity authCheck;
    private final PacketCheckVelocity packetCheck;
    private final AccountCheck accountCheck;
    private final SlowJoinCheck slowJoinCheck;
    private final LegalNameCheck legalNameCheck;
    private final InvalidNameCheck invalidNameCheck;
    private final RegisterCheck registerCheck;
    private final me.kr1s_d.ultimateantibot.common.service.VPNService VPNService;
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
        this.authCheck = new AuthCheckVelocity(antiBotPlugin);
        this.packetCheck = new PacketCheckVelocity(antiBotPlugin);
        this.accountCheck = new AccountCheck(antiBotPlugin);
        this.slowJoinCheck = new SlowJoinCheck(antiBotPlugin);
        this.legalNameCheck = new LegalNameCheck(antiBotPlugin);
        this.invalidNameCheck = new InvalidNameCheck(plugin);
        this.registerCheck = new RegisterCheck(plugin);
        this.VPNService = plugin.getVPNService();
        this.userDataService = plugin.getUserDataService();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPreLoginEvent(PreLoginEvent e) {
        //e.registerIntent(UltimateAntiBotBungeeCord.getInstance());
        String ip = Utils.getIP(e.getConnection().getRemoteAddress());
        String name = e.getUsername();
        int totals = blackListService.size() + queueService.size();
        if (blackListService.size() != 0 && totals != 0) {
            ServerUtil.blacklistPercentage = Math.round((float) blackListService.size() / totals * 100);
        }
        antiBotManager.increaseJoinPerSecond();

        //anti crash on attack start for first 5s
        if((System.currentTimeMillis() - ServerUtil.lastStartAttack) < 5000) {
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(Utils.colora(MessageManager.fastJoinQueueMessage)));
            return;
        }

        //
        //BlackList & Whitelist Checks
        //
        if (blackListService.isBlackListed(ip)) {
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(blacklistMSG(ip)));
            return;
        }
        if (whitelistService.isWhitelisted(ip)) {
            return;
        }

        //
        //AntiBotMode Enable
        //
        if (antiBotManager.getSpeedJoinPerSecond() >= ConfigManger.antiBotModeTrigger) {
            if (!antiBotManager.isAntiBotModeEnabled()) {
                antiBotManager.enableAntiBotMode();
                e.setResult(PreLoginEvent.PreLoginComponentResult.denied(KComponentBuilder.colorized(MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(ServerUtil.blacklistPercentage)))));
                return;
            }
        }

        //
        // NameChangerCheck
        //
        if (nameChangerCheck.isDenied(ip, name)) {
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(blacklistMSG(ip)));
            return;
        }

        //
        //Queue Service
        //
        if (!queueService.isQueued(ip) && !blackListService.isBlackListed(ip) && !whitelistService.isWhitelisted(ip)) {
            queueService.queue(ip);
        }

        //
        //Legal Name Check
        //
        if (legalNameCheck.isDenied(ip, name)) {
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(blacklistMSG(ip)));
            return;
        }
        //
        //FirstJoinCheck
        //
        if (firstJoinCheck.isDenied(ip, name)) {
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(KComponentBuilder.colorized(MessageManager.firstJoinMessage)));
            return;
        }

        //
        // Invalid Name Check
        //
        if (invalidNameCheck.isDenied(ip, name)) {
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(blacklistMSG(ip)));
            return;
        }

        //
        // SuperJoinCheck
        //
        if (superJoinCheck.isDenied(ip, name)) {
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_JOINS, name);
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(blacklistMSG(ip)));
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
            e.setResult(PreLoginEvent.PreLoginComponentResult.denied(KComponentBuilder.colorized(
                    MessageManager.getAntiBotModeMessage(String.valueOf(ConfigManger.authPercent), String.valueOf(ServerUtil.blacklistPercentage))
            )));
        }
    }

    @Subscribe
    public void onPostLoginEvent(PostLoginEvent e) {
        Player player = e.getPlayer();
        String nickname = player.getUsername();
        String ip = Utils.getIP(player);
        //
        //Packet Check
        //
        packetCheck.registerJoin(ip);
        //
        //connection profiles
        //
        userDataService.registerJoin(ip, nickname);
        //
        //Account Check
        //
        if (accountCheck.isDenied(ip, nickname)) {
            plugin.disconnect(ip, MessageManager.getAccountOnlineMessage());
            return;
        }
        //
        //SlowJoin check
        //
        if (slowJoinCheck.isDenied(ip, nickname)) {
            blackListService.blacklist(ip, BlackListReason.STRANGE_PLAYER, nickname);
            plugin.disconnect(ip, MessageManager.getSafeModeMessage());
            return;
        }

        //If isn't whitelisted
        if (!antiBotManager.getWhitelistService().isWhitelisted(ip)) {
            //Add to last join
            antiBotManager.getJoinCache().addJoined(ip);
            //Auto Whitelist Task
            plugin.scheduleDelayedTask(new AutoWhitelistTask(plugin, ip), false, 1000L * ConfigManger.playtimeForWhitelist * 60L);
            //Remove from JoinCache after 30 Seconds
            plugin.scheduleDelayedTask(() -> antiBotManager.getJoinCache().removeJoined(ip), false, 1000L * 30);
            //
            //Connection check (ProxyCheck.io or ip-api.com)
            //
            if (!player.hasPermission("uab.bypass.vpn")) {
                VPNService.submitIP(ip, nickname);
            }
        }
        //Notification
        if (player.hasPermission("uab.notification.automatic") && antiBotManager.isSomeModeOnline()) {
            Notificator.automaticNotification(player);
        }
    }

    @Subscribe
    public void onChat(PlayerChatEvent e) {
        String ip = Utils.getIP(e.getPlayer().getRemoteAddress());
        String nickname = e.getPlayer().getUsername();

        //
        //Register Check
        //
        registerCheck.onChat(ip, nickname, e.getMessage());
    }

    @Subscribe
    public void onPing(ProxyPingEvent e) {
        String ip = Utils.getIP(e.getConnection().getRemoteAddress());
        //
        //Auth Check Ping Action
        //
        if (ServerUtil.blacklistPercentage >= ConfigManger.authPercent && antiBotManager.isAntiBotModeEnabled()) {
            authCheck.onPing(e, ip);
        }
    }

    @Subscribe
    public void onUnLogin(DisconnectEvent e) {
        String ip = Utils.getIP(e.getPlayer());
        //
        //Packet Check
        //
        packetCheck.onUnLogin(ip);
        //
        //Account Check
        //
        accountCheck.onDisconnect(ip, e.getPlayer().getUsername());
        //
        //connection profiles
        //
        userDataService.registerQuit(ip);
        //
        //Invalid name check
        //
        invalidNameCheck.onDisconnect(ip, e.getPlayer().getUsername());
        //
        //Register check
        //
        registerCheck.onDisconnect(ip, e.getPlayer().getUsername());
    }

    @Subscribe
    public void onSettings(PlayerSettingsChangedEvent e) {
        String ip = Utils.getIP(e.getPlayer());
        //
        //PacketCheck
        //
        packetCheck.registerPacket(ip);
    }

    private Component blacklistMSG(String ip) {
        return Utils.colora(MessageManager.getBlacklistedMessage(blackListService.getProfile(ip)));
    }
}
