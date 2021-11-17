package me.kr1s_d.commons.helper;

import me.kr1s_d.commons.helper.enums.ColorHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {
    public Logger logger;

    public LogHelper(Logger logger){
        this.logger = logger;
    }

    public void debug(String msg){
        logger.log(Level.INFO, ColorHelper.colorize(msg));
    }

    public void warn(String msg){
        logger.log(Level.WARNING, ColorHelper.colorize(msg));
    }

    public void error(String msg){
        logger.log(Level.SEVERE, ColorHelper.colorize(msg));
    }
}
