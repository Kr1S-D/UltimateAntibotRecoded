package me.kr1s_d.ultimateantibot.common.core.thread;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

public class DynamicCounterThread {
    private final IAntiBotPlugin plugin;
    private long total;
    private long lastCount;
    private long count;

    public DynamicCounterThread(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.total = 0L;
        this.count = 0L;
        plugin.scheduleRepeatingTask(() -> {
            lastCount = count;
            count = 0;
        }, false, 1000L);
    }

    public long getSlowCount() {
        return lastCount;
    }

    public long getSpeedCount(){
        return count;
    }

    public void increase() {
        count++;
        if(plugin.getAntiBotManager().isSomeModeOnline()) total++;
    }

    public long getTotal() {
        return this.total;
    }

    public void resetTotal() {
        this.total = 0L;
    }
}