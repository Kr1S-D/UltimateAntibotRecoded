package me.kr1s_d.ultimateantibot.common.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String convertSeconds(long seconds) {
        long years = seconds / (60 * 60 * 24 * 365);
        seconds %= (60 * 60 * 24 * 365);
        long months = seconds / (60 * 60 * 24 * 30);
        seconds %= (60 * 60 * 24 * 30);
        long days = seconds / (60 * 60 * 24);
        seconds %= (60 * 60 * 24);
        long hours = seconds / (60 * 60);
        seconds %= (60 * 60);
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder result = new StringBuilder();

        if (years > 0) {
            result.append(years).append("y ");
        }
        if (months > 0) {
            result.append(months).append("m ");
        }
        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0) {
            result.append(minutes).append("m ");
        }
        if (seconds > 0) {
            result.append(seconds).append("s");
        }

        String output = result.toString().trim();
        return output.isEmpty() ? "0s" : output;
    }

    public static String formatMilliseconds(long millis) {
        return convertSeconds(TimeUnit.MILLISECONDS.toSeconds(millis));
    }
}
