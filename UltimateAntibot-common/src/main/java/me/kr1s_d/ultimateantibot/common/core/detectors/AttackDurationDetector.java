package me.kr1s_d.ultimateantibot.common.core.detectors;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

public class AttackDurationDetector extends AbstractDetector {
    private final IAntiBotPlugin plugin;
    private long duration;

    public AttackDurationDetector(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.duration = 0;
    }

    @Override
    public int getTickDelay() {
        return 1;
    }

    @Override
    public void tick() {
        if(plugin.getAntiBotManager().isSomeModeOnline()) {
            duration++;
            return;
        }

        duration = 0;
    }

    public void resetDuration() {
        this.duration = 0;
    }

    public long getAttackDuration() {
        return duration;
    }
}
