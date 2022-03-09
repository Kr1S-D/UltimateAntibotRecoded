package me.kr1s_d.ultimateantibot.common.objects.interfaces;

import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.server.SatelliteServer;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.thread.AttackAnalyzerThread;

public interface IAntiBotManager {
    long getChecksPerSecond();

    long getJoinPerSecond();

    long getPingPerSecond();

    long getPacketPerSecond();

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

    boolean isAntiBotModeEnabled();

    boolean isSlowAntiBotModeEnabled();

    boolean isPacketModeEnabled();

    boolean isPingModeEnabled();

    void enableAntiBotMode();

    void enableSlowAntiBotMode();

    void enablePacketMode();

    void enablePingMode();

    void updateTasks();

    boolean canDisable(ModeType modeType);

    JoinCache getJoinCache();

    String replaceInfo(String str);
}
