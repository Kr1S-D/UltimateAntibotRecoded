package me.kr1s_d.ultimateantibot.common.cache;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotAttackInfo;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;

public class AntiBotAttackInfo implements IAntiBotAttackInfo {

    private final IAntiBotManager antiBotManager;
    private int botPerSecond;
    private int joinPerSecond;
    private int pingPerSecond;
    private int packetPerSecond;
    private long totalBots;
    private long totalPings;
    private long totalPackets;

    public AntiBotAttackInfo(IAntiBotManager antiBotManager){
        this.antiBotManager = antiBotManager;
        this.botPerSecond = 0;
        this.joinPerSecond = 0;
        this.pingPerSecond = 0;
        this.packetPerSecond = 0;
        this.totalBots = 0;
        this.totalPings = 0;
        this.totalPackets = 0;
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
    public long getTotalBot() {
        return totalBots;
    }

    @Override
    public long getTotalPing() {
        return totalPings;
    }

    @Override
    public long getTotalPackets() {
        return totalPackets;
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
    public void setTotalBot(long value) {
        this.totalBots = value;
    }

    @Override
    public void setTotalPing(long value) {
        this.totalPings = value;
    }

    @Override
    public void setTotalPackets(long value) {
        this.totalPackets = value;
    }

    @Override
    public boolean isBypassAttack() {
        return antiBotManager.isAntiBotModeEnable() && pingPerSecond >= 20;
    }
}
