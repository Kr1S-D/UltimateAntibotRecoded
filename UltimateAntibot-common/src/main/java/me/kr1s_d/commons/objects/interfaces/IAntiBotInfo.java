package me.kr1s_d.commons.objects.interfaces;

public interface IAntiBotInfo {
    int getBotPerSecond();
    int getPingPerSecond();
    int getPacketPerSecond();
    long getTotalBot();
    long getTotalPing();
    long getTotalPackets();
    void setBotPerSecond(int value);
    void setPingPerSecond(int value);
    void setPacketPerSecond(int value);
    long setTotalBot(long value);
    long setTotalPing(long value);
    long setTotalPackets(long value);
}
