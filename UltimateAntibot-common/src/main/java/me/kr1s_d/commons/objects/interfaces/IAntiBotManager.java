package me.kr1s_d.commons.objects.interfaces;

import me.kr1s_d.commons.objects.enums.ModeType;
import me.kr1s_d.commons.service.BlackListService;
import me.kr1s_d.commons.service.QueueService;
import me.kr1s_d.commons.service.WhitelistService;

public interface IAntiBotManager {
    int getChecksPerSecond();

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

    boolean isAntiBotModeEnable();

    boolean isSlowAntiBotModeEnabled();

    boolean isPacketModeEnabled();

    boolean isPingModeEnabled();

    void enableAntiBotMode();

    void enableSlowAntiBotMode();

    void enablePacketMode();

    void enablePingMode();


}
