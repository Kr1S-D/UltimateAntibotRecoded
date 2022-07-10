package me.kr1s_d.ultimateantibot.common;

public interface UABRunnable extends Runnable{
    boolean isAsync();

    long getPeriod();
}
