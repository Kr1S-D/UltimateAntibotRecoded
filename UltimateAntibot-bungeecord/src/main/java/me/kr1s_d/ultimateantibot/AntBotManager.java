package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.cache.AntiBotAttackInfo;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotAttackInfo;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;

public class AntBotManager implements IAntiBotManager {
    private int checkPerSecond;
    private int joinPerSecond;
    private int botPerSecond;
    private int pingPerSecond;
    private int packetPerSecond;
    private final long totalPing;
    private final long totalBotBlocked;
    private final long totalPacketBlocked;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    private final QueueService queueService;
    private ModeType modeType;
    boolean isAntiBotModeOnline;
    boolean isSlowAntiBotModeOnline;
    boolean isPacketModeEnabled;
    boolean isPingModeEnabled;
    private final IAntiBotAttackInfo antiBotAttackInfo;
    private final LogHelper logHelper;

    public AntBotManager(IAntiBotPlugin plugin){
        this.checkPerSecond = 0;
        this.joinPerSecond = 0;
        this.botPerSecond = 0;
        this.pingPerSecond = 0;
        this.packetPerSecond = 0;
        this.totalPing = 0;
        this.totalBotBlocked = 0;
        this.totalPacketBlocked = 0;
        this.blackListService = new BlackListService(plugin.getBlackList());
        this.whitelistService = new WhitelistService(plugin.getWhitelist());
        this.queueService = new QueueService();
        this.modeType = ModeType.OFFLINE;
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.antiBotAttackInfo = new AntiBotAttackInfo(this);
        this.logHelper = plugin.getLogHelper();
    }

    @Override
    public int getChecksPerSecond() {
        return checkPerSecond;
    }

    @Override
    public int getBotPerSecond() {
        return botPerSecond;
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
    public void setBotPerSecond(int value) {
        this.botPerSecond = value;
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
    public boolean isAntiBotModeEnable() {
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

    }

    @Override
    public void enableSlowAntiBotMode() {

    }

    @Override
    public void enablePacketMode() {

    }

    @Override
    public void enablePingMode() {

    }

    @Override
    public void onCoreRefresh() {
        antiBotAttackInfo.setBotPerSecond(botPerSecond);
        antiBotAttackInfo.setJoinPerSecond(joinPerSecond);
        antiBotAttackInfo.setPingPerSecond(pingPerSecond);
        antiBotAttackInfo.setPacketPerSecond(packetPerSecond);
        this.checkPerSecond = 0;
        this.joinPerSecond = 0;
        this.botPerSecond = 0;
        this.pingPerSecond = 0;
        this.packetPerSecond = 0;
    }

    @Override
    public IAntiBotAttackInfo getAntiBotAttackInfo() {
        return antiBotAttackInfo;
    }
}
