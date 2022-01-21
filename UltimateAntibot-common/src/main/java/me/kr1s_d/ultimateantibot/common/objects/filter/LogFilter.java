package me.kr1s_d.ultimateantibot.common.objects.filter;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

    private final IAntiBotManager antiBotManager;
    private final List<String> blocked;

    public LogFilter(IAntiBotPlugin antiBotPlugin){

        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.blocked = new ArrayList<>(Arrays.asList(
                "InitialHandler has",
                "Connection reset by peer",
                "Unexpected packet received",
                "read timed out",
                "to process!",
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
                "Connect reset by peer"
        ));
        blocked.addAll(antiBotPlugin.getConfig().getStringList("filter"));
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if(antiBotManager.isSomeModeOnline()){
            if(record.getMessage().toLowerCase().contains("{0}")){
                antiBotManager.increasePacketPerSecond();
                return false;
            }
        }
        if(!isFiltered(record.getMessage()) && antiBotManager.getPacketPerSecond() >= ConfigManger.packetModeTrigger){
            if(!antiBotManager.isPacketModeEnabled()){
                antiBotManager.enablePacketMode();
            }
        }
        isFiltered(record.getMessage());
        return true;
    }

    public boolean isFiltered(String record){
        for(String str : blocked){
            if(record.toLowerCase().contains(str.toLowerCase())){
                antiBotManager.increasePacketPerSecond();
                return false;
            }
        }
        return true;
    }
}
