package me.kr1s_d.ultimateantibot.filter;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.util.List;

public class Velocity247Filter implements Filter{
    private final IAntiBotManager antiBotManager;
    private final List<String> blocked;

    public Velocity247Filter(IAntiBotPlugin antiBotPlugin){
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.blocked = antiBotPlugin.getConfigYml().getStringList("persistent-filter");
    }

    public Filter.Result checkMessage(String record) {
        if(antiBotManager.isSomeModeOnline()) return Filter.Result.NEUTRAL;
        for(String str : blocked){
            if(record.toLowerCase().contains(str.toLowerCase())){
                return Filter.Result.DENY;
            }
        }
        return Filter.Result.NEUTRAL;
    }

    @Override
    public Filter.Result getOnMismatch() {
        return Filter.Result.NEUTRAL;
    }

    @Override
    public Filter.Result getOnMatch() {
        return Filter.Result.NEUTRAL;
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return checkMessage(msg);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return checkMessage(message);
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return checkMessage(msg.toString());
    }

    @Override
    public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return checkMessage(msg.getFormattedMessage());
    }

    @Override
    public Filter.Result filter(LogEvent event) {
        return checkMessage(event.getMessage().getFormattedMessage());
    }

    @Override
    public LifeCycle.State getState() {
        return LifeCycle.State.INITIALIZED;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
