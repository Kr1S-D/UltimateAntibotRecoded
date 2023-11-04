package me.kr1s_d.ultimateantibot.common.utils;

import java.util.Arrays;
import java.util.List;

public class FilterUtils {
    public static void populateDefaultFilter(List<String> filter) {
        filter.addAll(Arrays.asList(
                "InitialHandler has",
                "Connection reset by peer",
                "Unexpected packet received",
                "read timed out",
                "could not decode packet",
                "to process",
                "Empty Packet!",
                "corrupted",
                "has pinged",
                "has connected",
                "in packet",
                "bad packet ID",
                "bad packet",
                "encountered exception",
                "com.mojang.authlib",
                "lost connection: Timed out",
                "lost connection: Disconnected",
                "Took too long to log in",
                "disconnected with",
                "read time out",
                "Connect reset by peer",
                "overflow in packet",
                "pipeline",
                "The received encoded string",
                "is longer than maximum allowed"
        ));
    }
}
