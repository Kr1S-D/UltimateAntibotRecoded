package me.kr1s_d.ultimateantibot.common.core.detectors;

import me.kr1s_d.ultimateantibot.common.AttackType;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AttackWatcherDetector extends AbstractDetector {
    private final IAntiBotManager manager;
    private final Set<AttackType> type;

    public AttackWatcherDetector(IAntiBotManager manager){
        this.manager = manager;
        this.type = new HashSet<>();
    }

    @Override
    public int getTickDelay() {
        return 1;
    }

    @Override
    public void tick() {
        type.clear(); //clear
        List<ModeType> enabledModes = manager.getEnabledModes();
        //execute checks
    }

    public List<AttackType> getFiredAttacks() {
        return new ArrayList<>(type);
    }
}
