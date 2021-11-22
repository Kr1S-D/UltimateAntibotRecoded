package me.kr1s_d.ultimateantibot.common.objects.filter;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

    private final IAntiBotManager antiBotManager;

    public LogFilter(IAntiBotManager antiBotManager){
        this.antiBotManager = antiBotManager;
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if(record.getLevel().equals(Level.SEVERE) && antiBotManager.isSomeModeOnline()){
            antiBotManager.increasePacketPerSecond();
            return false;
        }else {
            return !antiBotManager.isAntiBotModeEnabled() && !antiBotManager.isPingModeEnabled();
        }
    }
}
