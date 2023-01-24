package me.kr1s_d.ultimateantibot.common.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String formatSeconds(long tim) {
        long hours = tim / 3600;
        long minutes = (tim % 3600) / 60;
        long seconds = tim % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatMilliseconds(long millis) {
        return formatSeconds(TimeUnit.MILLISECONDS.toSeconds(millis));
    }
}
