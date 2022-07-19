package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

public class DynamicCounterThread{
    private long total;
    private long lastCount;
    private long count;

    public DynamicCounterThread(IAntiBotPlugin plugin) {
        plugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.total = 0L;
        this.count = 0L;
        plugin.scheduleRepeatingTask(() -> {
            lastCount = count;
            count = 0;
        }, false, 1000L);
    }

    public long getCount() {
        return lastCount;
    }

    public void increase() {
        total++;
        count++;
    }

    public long getTotal() {
        return this.total;
    }

    public void resetTotal() {
        this.total = 0L;
    }
}