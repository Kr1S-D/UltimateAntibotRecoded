package me.kr1s_d.commons.objects.interfaces;

import me.kr1s_d.commons.helper.LogHelper;

public interface IAntiBotPlugin {
    void scheduleSyncDelayedTask(Runnable runnable, boolean async, long milliseconds);

    IConfiguration getConfig();

    IConfiguration getMessages();

    IConfiguration getWhitelist();

    IConfiguration getBlackList();

    IConfiguration getDatabase();

    IAntiBotManager getAntiBotManager();

    LogHelper getLogHelper();
}
