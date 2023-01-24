package me.kr1s_d.ultimateantibot.common.detectors;

import me.kr1s_d.ultimateantibot.common.AttackType;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;

/**
 * Tagged as to be removed in the future
 */
@Deprecated
public class AttackTypeDetector extends AbstractDetector {
    @Override
    public int getTickDelay() {
        return 0;
    }

    @Override
    public void tick() {

    }
    /*private final IAntiBotManager manager;

    public AttackTypeDetector(IAntiBotPlugin plugin){
        this.manager = plugin.getAntiBotManager();
    }

    @Override
    public int getTickDelay() {
        return 2;
    }

    @Override
    public void tick() {
        ModeType current = manager.getModeType();

        if(current.equals(ModeType.SLOW)) {
            manager.setAttackType(AttackType.SLOW_JOIN);
            return;
        }
        if(current.equals(ModeType.PING)){
            manager.setAttackType(AttackType.MOTD);
            return;
        }
        if(manager.isPacketModeEnabled()){
            manager.setAttackType(AttackType.INVALID_PACKETS);
            return;
        }

        if(!manager.isAntiBotModeEnabled()) return;

        long cps = manager.getJoinPerSecond();
        long pps = manager.getPingPerSecond();

        long min = cps * 15 / 100;

        if(pps >= min && cps > 10){
            manager.setAttackType(AttackType.COMBINED);
            return;
        }

        manager.setAttackType(AttackType.JOIN);
    }*/
}
