package me.kr1s_d.ultimateantibot.common.objects.interfaces;

public interface IAntiBotAttackInfo {
    int getBotPerSecond();

    int getJoinPerSecond();

    int getPingPerSecond();

    int getPacketPerSecond();

    long getTotalBot();

    long getTotalPing();

    long getTotalPackets();

    void setBotPerSecond(int value);

    void setJoinPerSecond(int value);

    void setPingPerSecond(int value);

    void setPacketPerSecond(int value);

    void setTotalBot(long value);

    void setTotalPing(long value);

    void setTotalPackets(long value);

    boolean isBypassAttack();
}
