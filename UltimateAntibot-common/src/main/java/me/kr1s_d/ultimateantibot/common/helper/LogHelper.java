package me.kr1s_d.ultimateantibot.common.helper;

import me.kr1s_d.ultimateantibot.common.IServerPlatform;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;

import javax.print.DocFlavor;

import static me.kr1s_d.ultimateantibot.common.helper.LogHelper.LogType.*;

public class LogHelper {
    private final IServerPlatform platform;

    public LogHelper(IServerPlatform platform) {
        this.platform = platform;
    }

    public void debug(String msg) {
        if(!ConfigManger.isDebugModeOnline) return;
        platform.log(INFO, ServerUtil.colorize("&F[UAB DEBUG] &7» " + msg));
    }

    public void warn(String msg) {
        platform.log(WARNING, ServerUtil.colorize(MessageManager.prefix + msg));
    }

    public void error(String msg) {
        platform.log(ERROR, ServerUtil.colorize(MessageManager.prefix + "&c" + msg));
    }

    public void info(String msg) {
        platform.log(INFO, ServerUtil.colorize(MessageManager.prefix + msg));
    }

    public void sendLogo() {
        if(PerformanceHelper.getRunning().equals(ServerType.VELOCITY)) {
            sendVelocityLogo();
            return;
        }
        info("&c _    _         &c____ ");
        info("&c| |  | |§f  /\\   &c|  _ \\ ");
        info("&c| |  | |§f /  \\  &c| |_) |");
        info("&c| |  | |§f/ /\\ \\&c |  _ <");
        info("&c| |__| §f/ ____ \\&c| |_) |");
        info("&c\\____§f/_/     \\_&c\\____/");
    }

    public void sendVelocityLogo() {
        info("&c _    _          &c____ ");
        info("&c| |  | |§f  /\\    &c|  _ \\ ");
        info("&c| |  | |§f /  \\   &c| |_) |");
        info("&c| |  | |§f/ /\\ \\ &c |  _ <");
        info("&c| |__| §f/ ____ \\ &c| |_) |");
        info("&c\\____§f/_/     \\_ &c\\____/");
    }

    public enum LogType {
        INFO, WARNING, ERROR
    }
}
