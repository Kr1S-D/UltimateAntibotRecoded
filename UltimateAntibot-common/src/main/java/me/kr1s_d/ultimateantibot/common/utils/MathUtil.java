package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.FancyLong;
import me.kr1s_d.ultimateantibot.common.objects.PonderateEntry;

import java.util.concurrent.TimeUnit;

public class MathUtil {
    public static long convertToSeconds(long milliseconds) {
        return TimeUnit.MILLISECONDS.toSeconds(milliseconds);
    }

    public static long pastedSeconds(long milliseconds) {
        return convertToSeconds(System.currentTimeMillis() - milliseconds);
    }

    public static long pastedMillis(long milliseconds) {
        return System.currentTimeMillis() - milliseconds;
    }

    public static FancyLong pastedFancyMillis(long milliseconds) {
        return new FancyLong(pastedMillis(milliseconds));
    }

    public static FancyLong max(FancyLong a, FancyLong b) {
        return new FancyLong(Math.max(a.get(), b.get()));
    }

    public static FancyLong min(FancyLong a, FancyLong b) {
        return new FancyLong(Math.min(a.get(), b.get()));
    }

    public static int multiplyDouble(double d1, double d2) {
        return (int) Math.round(d1 * d2);
    }

    public static double ponderate(PonderateEntry... params) {
        double sumValuesTimesWeights = 0;
        double sumWeights = 0;

        for (PonderateEntry entry : params) {
            sumValuesTimesWeights += entry.getValue() * entry.getWeight();
            sumWeights += entry.getWeight();
        }

        return sumValuesTimesWeights / sumWeights;
    }
}
