package me.kr1s_d.ultimateantibot.common.objects.filter;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

    private final IAntiBotManager antiBotManager;
    private List<String> blocked;

    public LogFilter(IAntiBotManager antiBotManager){
        this.antiBotManager = antiBotManager;
        this.blocked = Arrays.asList();
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if(record.getLevel().equals(Level.SEVERE) && antiBotManager.isPacketModeEnabled() || record.getLevel().equals(Level.WARNING) && antiBotManager.isPacketModeEnabled()){
            antiBotManager.increasePacketPerSecond();
        }
        return !record.getMessage().contains("{0}");
    }
}
