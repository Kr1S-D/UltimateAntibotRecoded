package me.kr1s_d.ultimateantibot.common.helper;

import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {
    private final Logger logger;

    public LogHelper(Logger logger){
        this.logger = logger;
    }

    public void debug(String msg){
        if(!ConfigManger.isDebugModeOnline) return;
        logger.log(Level.INFO, ColorHelper.colorize("&F[UAB DEBUG] &7» " + msg));
    }

    public void warn(String msg){
        logger.log(Level.WARNING, ColorHelper.colorize(MessageManager.prefix + msg));
    }

    public void error(String msg){
        logger.log(Level.SEVERE, ColorHelper.colorize(MessageManager.prefix + msg));
    }

    public void info(String msg){
        logger.log(Level.INFO, ColorHelper.colorize(MessageManager.prefix + msg));
    }

    public void sendLogo(){
        debug(MessageManager.prefix + "&a _    _         ____ ");
        debug(MessageManager.prefix + "&a| |  | |  /\\   |  _ \\ ");
        debug(MessageManager.prefix + "&a| |  | | /  \\  | |_) |");
        debug(MessageManager.prefix + "&a| |  | |/ /\\ \\ |  _ <");
        debug(MessageManager.prefix + "&a| |__| / ____ \\| |_) |");
        debug(MessageManager.prefix + "&a\\____/_/     \\_\\____/");
    }
}
