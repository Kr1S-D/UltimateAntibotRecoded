package me.kr1s_d.ultimateantibot.common.objects.filter;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

    private final IAntiBotManager antiBotManager;
    private final List<String> blocked;

    public LogFilter(IAntiBotPlugin antiBotPlugin){
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.blocked = Arrays.asList(
                "nitialHandler has",
                "Connection reset by peer",
                "Unexpected packet received",
                "read timed out",
                "to process!",
                "to process",
                "Empty Packet!",
                "corrupted",
                "has pinged",
                "has connected",
                "bad packet ID",
                "bad packet",
                "encountered exception",
                "com.mojang.authlib",
                "lost connection: Timed out",
                "lost connection: Disconnected",
                "Took too long to log in",
                "disconnected with"
        );
        blocked.addAll(antiBotPlugin.getMessages().getStringList("filter"));
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        for(String blocked : blocked){
            if(record.getMessage().toLowerCase().contains(blocked.toLowerCase())){
                if(!antiBotManager.isPacketModeEnabled()) antiBotManager.enablePacketMode();
                return false;
            }
        }
        if(antiBotManager.isPacketModeEnabled()){
            antiBotManager.increasePacketPerSecond();
            return false;
        }
        return true;
    }
}
