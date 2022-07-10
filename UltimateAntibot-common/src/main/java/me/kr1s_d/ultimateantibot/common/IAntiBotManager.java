package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;

public interface IAntiBotManager {
    long getChecksPerSecond();

    long getJoinPerSecond();

    long getPingPerSecond();

    long getPacketPerSecond();

    long getConnectionPerSecond();

    BlackListService getBlackListService();

    QueueService getQueueService();

    WhitelistService getWhitelistService();

    ModeType getModeType();

    void setModeType(ModeType type);

    void disableAll();

    void disableMode(ModeType type);

    boolean isSomeModeOnline();

    void increaseChecksPerSecond();

    void increaseJoinPerSecond();

    void increasePingPerSecond();

    void increasePacketPerSecond();

    void increaseConnectionPerSecond();

    boolean isAntiBotModeEnabled();

    boolean isSlowAntiBotModeEnabled();

    boolean isPacketModeEnabled();

    boolean isPingModeEnabled();

    void enableAntiBotMode();

    void enableSlowAntiBotMode();

    void enablePacketMode();

    void enablePingMode();

    void dispatchConsoleAttackMessage();

    boolean canDisable(ModeType modeType);

    JoinCache getJoinCache();

    String replaceInfo(String str);
}
