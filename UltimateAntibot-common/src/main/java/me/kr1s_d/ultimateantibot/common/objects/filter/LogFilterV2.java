package me.kr1s_d.ultimateantibot.common.objects.filter;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilterV2 implements Filter {

    private final IAntiBotManager antiBotManager;
    private final List<String> blocked;

    public LogFilterV2(IAntiBotPlugin antiBotPlugin) {

        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.blocked = new ArrayList<>(Arrays.asList(
                "InitialHandler has",
                "Connection reset by peer",
                "Unexpected packet received",
                "read timed out",
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
                "overflow in packet"
        ));
        blocked.addAll(antiBotPlugin.getConfigYml().getStringList("filter"));
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if (antiBotManager.isSomeModeOnline()) {
            if(antiBotManager.isPacketModeEnabled()) antiBotManager.increasePacketPerSecond();
            return !record.getMessage().toLowerCase().contains("{0}");
        }
        if(isDenied(record.getMessage())){
            antiBotManager.increasePacketPerSecond();
            if(antiBotManager.getPacketPerSecond() > ConfigManger.packetModeTrigger) antiBotManager.enablePacketMode();
        }
        return true;
    }


    public boolean isDenied(String record) {
        for (String str : blocked) {
            if (record.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
