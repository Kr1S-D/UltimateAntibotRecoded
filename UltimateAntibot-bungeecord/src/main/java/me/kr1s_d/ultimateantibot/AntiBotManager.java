package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.cache.AntiBotAttackInfo;
import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotAttackInfo;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.events.custom.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.task.ModeDisableTask;
import me.kr1s_d.ultimateantibot.utils.EventCaller;
import me.kr1s_d.ultimateantibot.utils.Utils;

public class AntiBotManager implements IAntiBotManager {
    private final IAntiBotPlugin iAntiBotPlugin;
    private int checkPerSecond;
    private int joinPerSecond;
    private int pingPerSecond;
    private int packetPerSecond;
    private long totalPing;
    private long totalBotBlocked;
    private long totalPacketBlocked;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    private final QueueService queueService;
    private ModeType modeType;
    private boolean isAntiBotModeOnline;
    private boolean isSlowAntiBotModeOnline;
    private boolean isPacketModeEnabled;
    private boolean isPingModeEnabled;
    private final IAntiBotAttackInfo antiBotAttackInfo;
    private final LogHelper logHelper;
    private final JoinCache joinCache;

    public AntiBotManager(IAntiBotPlugin plugin){
        this.iAntiBotPlugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.checkPerSecond = 0;
        this.joinPerSecond = 0;
        this.pingPerSecond = 0;
        this.packetPerSecond = 0;
        this.totalPing = 0;
        this.totalBotBlocked = 0;
        this.totalPacketBlocked = 0;
        this.blackListService = new BlackListService(plugin.getBlackList(), logHelper);
        this.whitelistService = new WhitelistService(plugin.getWhitelist(), logHelper);
        this.queueService = new QueueService();
        this.modeType = ModeType.OFFLINE;
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.antiBotAttackInfo = new AntiBotAttackInfo(this);
        this.joinCache = new JoinCache(plugin);
    }

    @Override
    public int getChecksPerSecond() {
        return checkPerSecond;
    }

    @Override
    public int getJoinPerSecond() {
        return joinPerSecond;
    }

    @Override
    public int getPingPerSecond() {
        return pingPerSecond;
    }

    @Override
    public int getPacketPerSecond() {
        return packetPerSecond;
    }

    @Override
    public long getTotalPing() {
        return totalPing;
    }

    @Override
    public long getTotalBotBlocked() {
        return totalBotBlocked;
    }

    @Override
    public long getTotalPackedBlocked() {
        return totalPacketBlocked;
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
    public void setCheckPerSecond(int value) {
        this.checkPerSecond = value;
    }

    @Override
    public void setJoinPerSecond(int value) {
        this.joinPerSecond = value;
    }

    @Override
    public void setPingPerSecond(int value) {
        this.pingPerSecond = value;
    }

    @Override
    public void setPacketPerSecond(int value) {
        this.packetPerSecond = value;
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
        checkPerSecond++;
    }

    @Override
    public void increaseJoinPerSecond() {
        joinPerSecond++;
    }

    @Override
    public void increasePingPerSecond() {
        pingPerSecond++;
    }

    @Override
    public void increasePacketPerSecond() {
        packetPerSecond++;
    }

    @Override
    public void increaseTotalBots() {
        totalBotBlocked++;
    }

    @Override
    public void increaseTotalPings() {
        totalPing++;
    }

    @Override
    public void increaseTotalPackets() {
        totalPacketBlocked++;
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
        antiBotAttackInfo.setCheckPerSecond(checkPerSecond);
        antiBotAttackInfo.setJoinPerSecond(joinPerSecond);
        antiBotAttackInfo.setPingPerSecond(pingPerSecond);
        antiBotAttackInfo.setPacketPerSecond(packetPerSecond);
        if (isSlowAntiBotModeOnline) {
            if (modeType.equals(ModeType.PACKETS)) {
                logHelper.info(replaceInfo(MessageManager.actionbarPackets));
            } else {
                logHelper.info(replaceInfo(MessageManager.actionbarAntiBotMode));
            }
        }
        this.checkPerSecond = 0;
        this.joinPerSecond = 0;
        this.pingPerSecond = 0;
        this.packetPerSecond = 0;
    }

    @Override
    public IAntiBotAttackInfo getAntiBotAttackInfo() {
        return antiBotAttackInfo;
    }

    @Override
    public boolean canDisable(ModeType modeType) {
        if(modeType.equals(ModeType.ANTIBOT)){
            return joinPerSecond > ConfigManger.antiBotModeTrigger;
        }
        if(modeType.equals(ModeType.SLOW)){
            return joinPerSecond > ConfigManger.antiBotModeTrigger;
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
                .replace("%bots%", String.valueOf(antiBotAttackInfo.getJoinPerSecond()))
                .replace("%pings%", String.valueOf(antiBotAttackInfo.getPingPerSecond()))
                .replace("%checks%", String.valueOf(antiBotAttackInfo.getCheckPerSecond()))
                .replace("%queue%", String.valueOf(queueService.size()))
                .replace("%whitelist%", String.valueOf(whitelistService.size()))
                .replace("%blacklist%", String.valueOf(blackListService.size()))
                .replace("%type%", String.valueOf(modeType.toString()))
                .replace("%packets%", String.valueOf(antiBotAttackInfo.getPacketPerSecond()))
                .replace("%totalbots%", String.valueOf(totalBotBlocked))
                .replace("%totalpings%", String.valueOf(totalPing))
                .replace("%totalpackets%", String.valueOf(totalPacketBlocked))
                .replace("%totalchecks%", String.valueOf(antiBotAttackInfo.getCheckPerSecond()))
                ;
    }
}
