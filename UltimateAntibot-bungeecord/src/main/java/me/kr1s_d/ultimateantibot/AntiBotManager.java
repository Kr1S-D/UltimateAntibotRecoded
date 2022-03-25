package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.ConnectionCheckerService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.thread.DynamicCounterThread;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.events.custom.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.task.ModeDisableTask;
import me.kr1s_d.ultimateantibot.utils.EventCaller;

public class AntiBotManager implements IAntiBotManager {
    private final IAntiBotPlugin iAntiBotPlugin;
    private final DynamicCounterThread joinPerSecond;
    private final DynamicCounterThread pingPerSecond;
    private final DynamicCounterThread packetPerSecond;
    private final DynamicCounterThread checkPerSecond;
    private final DynamicCounterThread connectionPerSecond;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    private final QueueService queueService;
    private ModeType modeType;
    private boolean isAntiBotModeOnline;
    private boolean isSlowAntiBotModeOnline;
    private boolean isPacketModeEnabled;
    private boolean isPingModeEnabled;
    private final LogHelper logHelper;
    private final JoinCache joinCache;
    private final ConnectionCheckerService connectionCheckerService;

    public AntiBotManager(IAntiBotPlugin plugin){
        this.iAntiBotPlugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.checkPerSecond = new DynamicCounterThread(plugin);
        this.joinPerSecond = new DynamicCounterThread(plugin);
        this.pingPerSecond = new DynamicCounterThread(plugin);
        this.packetPerSecond = new DynamicCounterThread(plugin);
        this.connectionPerSecond = new DynamicCounterThread(plugin);
        this.blackListService = new BlackListService(plugin.getBlackList(), logHelper);
        this.whitelistService = new WhitelistService(plugin.getWhitelist(), logHelper);
        this.queueService = new QueueService();
        this.modeType = ModeType.OFFLINE;
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.joinCache = new JoinCache(plugin);
        this.connectionCheckerService = plugin.getConnectionCheckerService();
    }

    @Override
    public long getChecksPerSecond() {
        return checkPerSecond.getCount();
    }

    @Override
    @Deprecated
    public long getJoinPerSecond() {
        return joinPerSecond.getCount();
    }

    @Override
    public long getPingPerSecond() {
        return pingPerSecond.getCount();
    }

    @Override
    public long getPacketPerSecond() {
        return packetPerSecond.getCount();
    }

    @Override
    public long getConnectionPerSecond() {
        return connectionPerSecond.getCount();
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
    public ModeType getModeType() {
        return modeType;
    }

    @Override
    public void setModeType(ModeType type) {
        this.modeType = type;
    }

    @Override
    public void disableAll() {
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.modeType = ModeType.OFFLINE;
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
        if(type.equals(ModeType.PING)){
            this.isPingModeEnabled = false;
        }
        this.modeType = ModeType.OFFLINE;
    }

    @Override
    public boolean isSomeModeOnline() {
        return isAntiBotModeOnline || isSlowAntiBotModeOnline || isPacketModeEnabled || isPingModeEnabled;
    }

    @Override
    public void increaseChecksPerSecond() {
        checkPerSecond.increase();
        increaseConnectionPerSecond();
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
        setModeType(ModeType.ANTIBOT);
        isAntiBotModeOnline = true;
        isSlowAntiBotModeOnline = false;
        isPingModeEnabled = false;
        isPacketModeEnabled = false;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.ANTIBOT),
                false, 1000L * ConfigManger.antiBotModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.ANTIBOT));
    }

    @Override
    public void enableSlowAntiBotMode() {
        setModeType(ModeType.SLOW);
        isAntiBotModeOnline = false;
        isSlowAntiBotModeOnline = true;
        isPingModeEnabled = false;
        isPacketModeEnabled = false;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.SLOW),
                false, 1000L * ConfigManger.antiBotModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.SLOW));
    }

    @Override
    public void enablePacketMode() {
        setModeType(ModeType.PACKETS);
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
        setModeType(ModeType.PING);
        isAntiBotModeOnline = false;
        isSlowAntiBotModeOnline = false;
        isPingModeEnabled = true;
        isPacketModeEnabled = false;
        iAntiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(iAntiBotPlugin, ModeType.PING),
                false, 1000L * ConfigManger.pingModeKeep
        );
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.PING));
    }

    @Override
    public void updateTasks() {
        if (isAntiBotModeOnline || isPingModeEnabled || isSlowAntiBotModeOnline) {
            logHelper.info(replaceInfo(MessageManager.actionbarAntiBotMode.replace("%prefix%", "")));
        }else{
            if(isPacketModeEnabled){
                logHelper.info(replaceInfo(MessageManager.actionbarPackets.replace("%prefix%", "")));
            }
        }
    }

    @Override
    public boolean canDisable(ModeType modeType) {
        if(modeType.equals(ModeType.ANTIBOT) || modeType.equals(ModeType.SLOW)){
            return joinPerSecond.getCount() <= ConfigManger.antiBotModeTrigger;
        }
        if(modeType.equals(ModeType.PING)){
            return pingPerSecond.getCount() <= ConfigManger.pingModeTrigger;
        }
        if (modeType.equals(ModeType.PACKETS)) {
            return packetPerSecond.getCount() < ConfigManger.packetModeTrigger;
        }
        return false;
    }

    @Override
    public JoinCache getJoinCache() {
        return joinCache;
    }

    @Override
    public String replaceInfo(String str) {
        return str
                .replace("%bots%", String.valueOf(joinPerSecond.getCount()))
                .replace("%pings%", String.valueOf(pingPerSecond.getCount()))
                .replace("%checks%", String.valueOf(checkPerSecond.getCount()))
                .replace("%queue%", String.valueOf(queueService.size()))
                .replace("%whitelist%", String.valueOf(whitelistService.size()))
                .replace("%blacklist%", String.valueOf(blackListService.size()))
                .replace("%type%", String.valueOf(modeType.toString()))
                .replace("%packets%", String.valueOf(packetPerSecond.getCount()))
                .replace("%totalbots%", String.valueOf(this.joinPerSecond.getTotal()))
                .replace("%totalpings%", String.valueOf(this.pingPerSecond.getTotal()))
                .replace("%totalpackets%", String.valueOf(this.packetPerSecond.getTotal()))
                .replace("%latency%", iAntiBotPlugin.getLatencyThread().getLatency())
                .replace("%prefix%", iAntiBotPlugin.getAnimationThread().getEmote() + " " + MessageManager.prefix)
                .replace("%underverification%", String.valueOf(connectionCheckerService.getUnderVerificationSize()))
                ;
    }
}
