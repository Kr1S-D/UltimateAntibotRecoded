package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;
import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.core.detectors.AttackDurationDetector;
import me.kr1s_d.ultimateantibot.common.core.detectors.AttackWatcherDetector;
import me.kr1s_d.ultimateantibot.common.core.thread.DynamicCounterThread;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.Formatter;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.event.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.scheduler.task.ModeDisableTask;
import me.kr1s_d.ultimateantibot.utils.EventCaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntiBotManager implements IAntiBotManager {
    private final IAntiBotPlugin iAntiBotPlugin;
    private final DynamicCounterThread joinPerSecond;
    private final DynamicCounterThread pingPerSecond;
    private final DynamicCounterThread packetPerSecond;
    private final DynamicCounterThread connectionPerSecond;
    private final AttackDurationDetector attackDurationDetector;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    private final QueueService queueService;
    private final List<ModeType> modeType;
    private boolean isAntiBotModeOnline;
    private boolean isSlowAntiBotModeOnline;
    private boolean isPacketModeEnabled;
    private boolean isPingModeEnabled;
    private final LogHelper logHelper;
    private final JoinCache joinCache;
    private final VPNService VPNService;
    private final AttackWatcherDetector attackDetector;

    public AntiBotManager(IAntiBotPlugin plugin) {
        this.iAntiBotPlugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.joinPerSecond = new DynamicCounterThread(plugin);
        this.pingPerSecond = new DynamicCounterThread(plugin);
        this.packetPerSecond = new DynamicCounterThread(plugin);
        this.connectionPerSecond = new DynamicCounterThread(plugin);
        this.attackDurationDetector = new AttackDurationDetector(plugin);
        this.queueService = new QueueService();
        this.blackListService = new BlackListService(plugin, queueService, plugin.getBlackList(), logHelper);
        this.whitelistService = new WhitelistService(queueService, plugin.getWhitelist(), logHelper);
        this.modeType = new ArrayList<>();
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.joinCache = new JoinCache();
        this.VPNService = plugin.getVPNService();
        this.attackDetector = new AttackWatcherDetector(this);
    }

    @Override
    public long getJoinPerSecond() {
        return joinPerSecond.getSlowCount();
    }

    @Override
    public long getSpeedJoinPerSecond() {
        return joinPerSecond.getSpeedCount();
    }

    @Override
    public long getPingPerSecond() {
        return pingPerSecond.getSlowCount();
    }

    @Override
    public long getPacketPerSecond() {
        return packetPerSecond.getSlowCount();
    }

    @Override
    public long getConnectionPerSecond() {
        return connectionPerSecond.getSlowCount();
    }

    @Override
    public long getAttackDuration() {
        return attackDurationDetector.getAttackDuration();
    }

    @Override
    public DynamicCounterThread getDynamicJoins(){
        return joinPerSecond;
    }

    @Override
    public DynamicCounterThread getDynamicPings() {
        return pingPerSecond;
    }

    @Override
    public DynamicCounterThread getDynamicPackets() {
        return packetPerSecond;
    }

    @Override
    public BlackListService getBlackListService() {
        return blackListService;
    }

    @Override
    public QueueService getQueueService() {
        return queueService;
    }

    @Override
    public WhitelistService getWhitelistService() {
        return whitelistService;
    }

    @Override
    public void disableAll() {
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.modeType.clear();
    }

    @Override
    public void disableMode(ModeType type) {
        if(type.equals(ModeType.ANTIBOT)) {
            this.isAntiBotModeOnline = false;
        }
        if(type.equals(ModeType.SLOW)) {
            this.isSlowAntiBotModeOnline = false;
        }
        if(type.equals(ModeType.PACKETS)) {
            this.isPacketModeEnabled = false;
        }
        if(type.equals(ModeType.PING)) {
            this.isPingModeEnabled = false;
        }

        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.OFFLINE));
        modeType.remove(type);
    }

    @Override
    public boolean isSomeModeOnline() {
        return isAntiBotModeOnline || isSlowAntiBotModeOnline || isPacketModeEnabled || isPingModeEnabled;
    }

    @Override
    public void increaseJoinPerSecond() {
        joinPerSecond.increase();
        increaseConnectionPerSecond();
    }

    @Override
    public void increasePingPerSecond() {
        pingPerSecond.increase();
        increaseConnectionPerSecond();
    }

    @Override
    public void increasePacketPerSecond() {
        packetPerSecond.increase();
        increaseConnectionPerSecond();
    }

    @Override
    public void increaseConnectionPerSecond(){
        connectionPerSecond.increase();
    }

    @Override
    public boolean isAntiBotModeEnabled() {
        return isAntiBotModeOnline;
    }

    @Override
    public boolean isSlowAntiBotModeEnabled() {
        return isSlowAntiBotModeOnline;
    }

    @Override
    public boolean isPacketModeEnabled() {
        return isPacketModeEnabled;
    }

    @Override
    public boolean isPingModeEnabled() {
        return isPingModeEnabled;
    }

    @Override
    public void enableAntiBotMode() {
        markModeType(ModeType.ANTIBOT);
        isAntiBotModeOnline = true;
        //isSlowAntiBotModeOnline = false;
        //isPingModeEnabled = false;
        //isPacketModeEnabled = false;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.ANTIBOT),
                false, 1000L * ConfigManger.antiBotModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.ANTIBOT));
    }

    @Override
    public void enableSlowAntiBotMode() {
        markModeType(ModeType.SLOW);
        isAntiBotModeOnline = false;
        //isSlowAntiBotModeOnline = true;
        //isPingModeEnabled = false;
        //isPacketModeEnabled = false;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.SLOW),
                false, 1000L * ConfigManger.slowAntibotModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.SLOW));
    }

    @Override
    public void enablePacketMode() {
        markModeType(ModeType.PACKETS);
        isAntiBotModeOnline = false;
        isSlowAntiBotModeOnline = false;
        isPingModeEnabled = false;
        isPacketModeEnabled = true;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.PACKETS),
                false, 1000L * ConfigManger.packetModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.PACKETS));
    }

    @Override
    public void enablePingMode() {
        markModeType(ModeType.PING);
        //isAntiBotModeOnline = false;
        //isSlowAntiBotModeOnline = false;
        isPingModeEnabled = true;
        //isPacketModeEnabled = false;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.PING),
                false, 1000L * ConfigManger.pingModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.PING));
    }

    @Override
    public void dispatchConsoleAttackMessage() {
        if (isAntiBotModeOnline || isPingModeEnabled || isSlowAntiBotModeOnline) {
            logHelper.info(replaceInfo(MessageManager.actionbarAntiBotMode.replace("%prefix%", "")));
        }else{
            if(isPacketModeEnabled) {
                logHelper.info(replaceInfo(MessageManager.actionbarPackets.replace("%prefix%", "")));
            }
        }
    }

    @Override
    public boolean canDisable(ModeType modeType) {
        if(modeType.equals(ModeType.ANTIBOT) || modeType.equals(ModeType.SLOW)) {
            return joinPerSecond.getSlowCount() <= ConfigManger.antiBotModeTrigger;
        }
        if(modeType.equals(ModeType.PING)){
            return pingPerSecond.getSlowCount() <= ConfigManger.pingModeTrigger;
        }
        if (modeType.equals(ModeType.PACKETS)) {
            return packetPerSecond.getSlowCount() < ConfigManger.packetModeTrigger;
        }
        return false;
    }

    @Override
    public JoinCache getJoinCache() {
        return joinCache;
    }

    @Override
    public AttackWatcherDetector getAttackDetector() {
        return attackDetector;
    }

    @Override
    public List<ModeType> getEnabledModes() {
        if(isSomeModeOnline()) return modeType;
        return Arrays.asList(ModeType.OFFLINE);
    }

    @Override
    public String replaceInfo(String str) {
        return str
                .replace("%bots%", String.valueOf(joinPerSecond.getSlowCount()))
                .replace("%pings%", String.valueOf(pingPerSecond.getSlowCount()))
                .replace("%queue%", String.valueOf(queueService.size()))
                .replace("%whitelist%", String.valueOf(whitelistService.size()))
                .replace("%blacklist%", String.valueOf(blackListService.size()))
                .replace("%type%", getModeTypes())
                .replace("%packets%", String.valueOf(packetPerSecond.getSlowCount()))
                .replace("%totalbots%", String.valueOf(Formatter.format(joinPerSecond.getTotal())))
                .replace("%totalpings%", String.valueOf(Formatter.format(pingPerSecond.getTotal())))
                .replace("%totalpackets%", String.valueOf(Formatter.format(packetPerSecond.getTotal())))
                .replace("%latency%", iAntiBotPlugin.getLatencyThread().getLatency())
                .replace("%prefix%", iAntiBotPlugin.getAnimationThread().getEmote() + " " + MessageManager.prefix)
                .replace("%underverification%", String.valueOf(VPNService.getUnderVerificationSize()))
                ;
    }

    private String getModeTypes() {
        StringBuilder b = new StringBuilder();
        List<ModeType> enabledModes = getEnabledModes();
        int size = enabledModes.size();
        for (int i = 0; i < size; i++) {
            b.append(enabledModes.get(i).name());
            if (i < size - 1) {
                b.append(" ");
            }
        }
        return b.toString();
    }

    private void markModeType(ModeType type) {
        if(modeType.contains(type)) return;
        modeType.add(type);
    }
}