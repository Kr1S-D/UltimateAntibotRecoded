package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;

import java.io.File;

public interface IServerPlatform {
    String colorize(String text);

    void cancelTask(int id);

    void log(LogHelper.LogType type, String log);

    void broadcast(String message);

    File getDFolder();
}
