package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.core.detectors.AttackWatcherDetector;
import me.kr1s_d.ultimateantibot.common.core.thread.DynamicCounterThread;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;

import java.util.List;

public interface IAntiBotManager {

    long getJoinPerSecond();

    long getSpeedJoinPerSecond();

    long getPingPerSecond();

    long getPacketPerSecond();

    long getConnectionPerSecond();

    long getAttackDuration();

    DynamicCounterThread getDynamicJoins();

    DynamicCounterThread getDynamicPings();

    DynamicCounterThread getDynamicPackets();

    BlackListService getBlackListService();

    QueueService getQueueService();

    WhitelistService getWhitelistService();

    void disableAll();

    void disableMode(ModeType type);

    boolean isSomeModeOnline();

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

    AttackWatcherDetector getAttackDetector();

    List<ModeType> getEnabledModes();

    String replaceInfo(String str);
}
