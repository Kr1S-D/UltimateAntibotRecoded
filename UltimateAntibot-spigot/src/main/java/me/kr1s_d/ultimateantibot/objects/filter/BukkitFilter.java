package me.kr1s_d.ultimateantibot.objects.filter;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BukkitFilter implements Filter {
    private final IAntiBotManager antiBotManager;
    private final List<String> blocked;

    public BukkitFilter(IAntiBotPlugin antiBotPlugin){

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
                "Connect reset by peer",
                "overflow in packet"
        ));
        blocked.addAll(antiBotPlugin.getConfigYml().getStringList("filter"));
    }

    public Result checkMessage(String record) {
        for(String str : blocked){
            if(record.contains(str)){
                return Result.DENY;
            }
        }
        return Result.NEUTRAL;
    }


    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return checkMessage(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return checkMessage(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return checkMessage(msg.toString());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return checkMessage(msg.getFormattedMessage());
    }

    @Override
    public Result filter(LogEvent event) {
        return checkMessage(event.getMessage().getFormattedMessage());
    }

    @Override
    public State getState() {
        return State.INITIALIZED;
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
