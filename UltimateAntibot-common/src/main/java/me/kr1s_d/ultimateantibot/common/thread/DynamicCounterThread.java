package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class DynamicCounterThread{
    private final List<Long> entries;
    private long total;
    private long count;
    private final long nanosecondInSeconds;

    public DynamicCounterThread(IAntiBotPlugin plugin) {
        plugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.entries = new CopyOnWriteArrayList<>();
        this.total = 0L;
        this.count = 0L;
        this.nanosecondInSeconds = TimeUnit.SECONDS.toNanos(1L);
        new Thread(() -> {
            while (plugin.isRunning()) {
                this.entries.removeIf(n -> System.nanoTime() - n >= this.nanosecondInSeconds);
                try {
                    Thread.sleep(250L);
                }
                catch (InterruptedException ex) {}
            }
        }, "UAB#DynamicCounter").start();
        new Thread(() -> {
            while (plugin.isRunning()) {
                this.count = this.entries.stream().filter(n2 -> System.nanoTime() - n2 < this.nanosecondInSeconds).count();
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex2) {}
            }
        }).start();
    }

    public long getCount() {
        return this.count;
    }

    public void increase() {
        this.entries.add(System.nanoTime());
        ++this.total;
    }

    public long getTotal() {
        return this.total;
    }

    public void resetTotal() {
        this.total = 0L;
    }
}