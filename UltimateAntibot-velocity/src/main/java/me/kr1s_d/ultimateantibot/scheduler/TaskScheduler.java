package me.kr1s_d.ultimateantibot.scheduler;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskScheduler {
    private static final Map<Long, ScheduledTask> scheduled = new ConcurrentHashMap<>();
    private static long current = 0;

    public static void cancelTrackedTask(long taskID) {
        checkTasks();
        ScheduledTask task = scheduled.getOrDefault(taskID, null);
        if(task == null) return;
        scheduled.remove(taskID);
        task.cancel();
    }

    public static long trackTask(ScheduledTask task) {
        checkTasks();
        current++;
        scheduled.put(current, task);
        return current;
    }

    private static void checkTasks() {
        for (Map.Entry<Long, ScheduledTask> entry : scheduled.entrySet()) {
            if(entry.getValue().status().equals(TaskStatus.CANCELLED) || entry.getValue().status().equals(TaskStatus.FINISHED)) {
                scheduled.remove(entry.getKey());
            }
        }
    }
}
