package me.kr1s_d.ultimateantibot.common.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Formatter {
    public static String format(float value) {
        String[] arr = {"", "k", "M", "B", "T", "Q", "KQ"};

        int i= 0;
        while ((value / 1000) >= 1) {
            value /= 1000;
            i++;
        }

        if(i >= arr.length) {
            i = 5;
        }

        return String.format("%s%s", new DecimalFormat("###.##", new DecimalFormatSymbols(Locale.ITALIAN)).format(value), arr[i]);
    }

    public static String format(double value) {
        String[] arr = {"", "k", "M", "B", "T", "Q", "KQ"};

        int i= 0;
        while ((value / 1000) >= 1) {
            value /= 1000;
            i++;
        }

        if(i >= arr.length) {
            i = 5;
        }

        return String.format("%s%s", new DecimalFormat("###.##", new DecimalFormatSymbols(Locale.ITALIAN)).format(value), arr[i]);
    }
}
