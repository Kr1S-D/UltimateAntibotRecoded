package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class DynamicCounterThread{
    private final List<Long> a;
    private long b;
    private long c;
    private final long n;

    public DynamicCounterThread(IAntiBotPlugin f) {
        this.a = new CopyOnWriteArrayList<Long>();
        this.b = 0L;
        this.c = 0L;
        this.n = TimeUnit.SECONDS.toNanos(1L);
        f.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");

        f.scheduleRepeatingTask(() -> a.removeIf(n -> System.nanoTime() - n >= n), false, 250L);
        f.scheduleRepeatingTask(() -> c = a.stream().filter(n2 -> System.nanoTime() - n2 < n).count(), false, 10L);
    }

    public long getCount() {
        return c;
    }

    public void increase() {
        b++;
        this.a.add(System.nanoTime());
    }

    public long getTotal() {
        return this.b;
    }

    public void resetTotal() {
        this.b = 0L;
    }
}