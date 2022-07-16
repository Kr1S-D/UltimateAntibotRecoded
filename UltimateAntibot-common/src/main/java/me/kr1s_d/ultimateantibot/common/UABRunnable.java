package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;

public abstract class UABRunnable implements Runnable {
    private int taskID;

    public abstract boolean isAsync();

    public abstract long getPeriod();

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public void cancel(){
        ServerUtil.cancelTask(this);
    }
}
