package me.kr1s_d.ultimateantibot.common.objects.interfaces;

import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;

public interface IAntiBotManager {
    int getChecksPerSecond();

    int getBotPerSecond();

    int getJoinPerSecond();

    int getPingPerSecond();

    int getPacketPerSecond();

    long getTotalPing();

    long getTotalBotBlocked();

    long getTotalPackedBlocked();

    BlackListService getBlackListService();

    QueueService getQueueService();

    WhitelistService getWhitelistService();

    ModeType getModeType();

    void setModeType(ModeType type);

    void setCheckPerSecond(int value);

    void setBotPerSecond(int value);

    void setJoinPerSecond(int value);

    void setPingPerSecond(int value);

    void setPacketPerSecond(int value);

    void disableAll();

    void disableMode(ModeType type);

    boolean isSomeModeOnline();

    void increaseChecksPerSecond();

    void increaseJoinPerSecond();

    void increasePingPerSecond();

    void increasePacketPerSecond();

    void increaseTotalBots();

    void increaseTotalPings();

    void increaseTotalPackets();

    boolean isAntiBotModeEnabled();

    boolean isSlowAntiBotModeEnabled();

    boolean isPacketModeEnabled();

    boolean isPingModeEnabled();

    void enableAntiBotMode();

    void enableSlowAntiBotMode();

    void enablePacketMode();

    void enablePingMode();

    void updateTasks();

    IAntiBotAttackInfo getAntiBotAttackInfo();

    boolean canDisable(ModeType modeType);

    JoinCache getJoinCache();

    String replaceInfo(String str);
}
