package me.kr1s_d.ultimateantibot.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BukkitWithFoliaScheduler {
    private final JavaPlugin plugin;
    private boolean isFolia;
    //FOR FOLIA ONLY
    private Map<Integer, ScheduledTask> taskMap = null;

    public BukkitWithFoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            isFolia = true;
            taskMap = new HashMap<>();
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }

    public int run(Runnable runnable, boolean async) {
        if (async) {
            return runAsync(runnable);
        }

        if (isFolia && taskMap != null) {
            int id = generateTaskId();
            taskMap.put(id, Bukkit.getGlobalRegionScheduler().run(this.plugin, task -> runnable.run()));
            return id;
        } else {
            return Bukkit.getScheduler().runTask(this.plugin, runnable).getTaskId();
        }
    }

    public int scheduleDelayed(Runnable runnable, boolean async, long delay, boolean unconverted) {
        long delay2 = unconverted ? Utils.convertToTicks(delay) : delay;
        if (isFolia && taskMap != null) {
            int id = generateTaskId();
            if (async) {
                taskMap.put(id, Bukkit.getAsyncScheduler().runDelayed(this.plugin, task -> runnable.run(), unconverted ? delay : Utils.convertToMillis(delay), TimeUnit.MILLISECONDS));
            } else {
                taskMap.put(id, Bukkit.getGlobalRegionScheduler().runDelayed(this.plugin, task -> runnable.run(), delay2));
            }
            return id;
        } else {
            return async ? Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, delay2).getTaskId() :
                    Bukkit.getScheduler().runTaskLater(this.plugin, runnable, delay2).getTaskId();
        }
    }

    public int scheduleRepeat(Runnable runnable, boolean async, long delay, long period, boolean unconverted) {
        long delay2 = unconverted ? Utils.convertToTicks(delay) : delay;
        long period2 = unconverted ? Utils.convertToTicks(period) : period;
        if (isFolia && taskMap != null) {
            int id = generateTaskId();
            if (async) {
                taskMap.put(id, Bukkit.getAsyncScheduler().runAtFixedRate(this.plugin, task -> runnable.run(), unconverted ? delay : Utils.convertToMillis(delay)
                        , unconverted ? period : Utils.convertToMillis(period), TimeUnit.MILLISECONDS));
            } else {
                taskMap.put(id, Bukkit.getGlobalRegionScheduler().runAtFixedRate(this.plugin, task -> runnable.run(), delay2, period2));
            }
            return id;
        } else {
            return async ? Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay2, period2).getTaskId() :
                            Bukkit.getScheduler().runTaskTimer(this.plugin, runnable, delay2, period2).getTaskId();
        }
    }

    private int runAsync(Runnable runnable) {
        if (isFolia && taskMap != null) {
            int id = generateTaskId();
            taskMap.put(id, Bukkit.getAsyncScheduler().runNow(this.plugin, task -> runnable.run()));
            return id;
        } else {
            return Bukkit.getScheduler().runTask(this.plugin, runnable).getTaskId();
        }
    }

    public void cancel(int id) {
        if (isFolia && taskMap != null) {
            taskMap.get(id).cancel();
            taskMap.remove(id);
        } else {
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    private int generateTaskId() {
        int last = 0;
        taskMap.keySet().forEach(i -> i = last);
        return last + 1;
    }
}
