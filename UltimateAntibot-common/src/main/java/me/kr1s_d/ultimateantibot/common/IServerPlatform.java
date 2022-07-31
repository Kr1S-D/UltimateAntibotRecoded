package me.kr1s_d.ultimateantibot.common;

import java.io.File;

public interface IServerPlatform {
    void cancelTask(int id);

    File getDFolder();
}
