package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public enum ModeType {
    ANTIBOT(4),
    SLOW(3),
    PACKETS(2),
    PING(1),
    OFFLINE(0);

    private final int priority;

    ModeType(int i) {
        this.priority = i;
    }

    public int getKeepValue() {
        switch (this) {
            case PING:
                return ConfigManger.pingModeKeep;
            case SLOW:
                return (int) ConfigManger.slowAntibotModeKeep;
            case PACKETS:
                return ConfigManger.packetModeKeep;
            case ANTIBOT:
                return ConfigManger.antiBotModeKeep;
            case OFFLINE:
                throw new UnsupportedOperationException("Offline mode hasn't a keep value!");
        }

        throw new UnsupportedOperationException("Offline mode hasn't a keep value!");
    }

    public boolean checkConditions(long jps, long pps, long pac) {
        switch (this) {
            case PING:
                return pps >= ConfigManger.pingModeTrigger;
            case ANTIBOT:
                return jps >= ConfigManger.antiBotModeTrigger;
            case PACKETS:
                return jps >= ConfigManger.packetModeTrigger;
            case SLOW:
            case OFFLINE:
                return false;
        }

        return false;
    }

    public int getPriority() {
        return priority;
    }
}
