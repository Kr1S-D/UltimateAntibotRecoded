package me.kr1s_d.ultimateantibot.common.core;

import me.kr1s_d.ultimateantibot.common.AttackType;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.ModeType;
import me.kr1s_d.ultimateantibot.common.core.server.CloudConfig;

import java.util.*;

public class AttackWatcher {
    private final IAntiBotManager manager;

    public AttackWatcher(IAntiBotManager manager) {
        this.manager = manager;
    }

    public List<AttackType> getFiredAttacks() {
        Set<AttackType> type = new HashSet<>();
        ModeType mode = manager.getModeType();
        long jps = manager.getJoinPerSecond();
        long pps = manager.getPingPerSecond();

        switch (mode) {
            case PING:
                if(pps > 2) type.add(AttackType.MOTD);
                break;
            case SLOW:
                type.add(AttackType.SLOW_JOIN);
                break;
            case ANTIBOT:
                if(pps <= CloudConfig.i) {
                    type.add(AttackType.JOIN_NO_PING);
                }else{
                    type.add(AttackType.COMBINED);
                }
                break;
            case PACKETS:
                type.add(AttackType.INVALID_PACKETS);
                break;
            case OFFLINE:
                return Collections.singletonList(AttackType.NONE);
        }

        return new ArrayList<>(type);
    }
}
