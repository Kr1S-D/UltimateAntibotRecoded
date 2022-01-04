package me.kr1s_d.ultimateantibot.common.objects.interfaces;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;

public interface IAntiBotPlugin {
    void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds);

    void runTask(Runnable task, boolean isAsync);

    void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds);

    IConfiguration getConfig();

    IConfiguration getMessages();

    IConfiguration getWhitelist();

    IConfiguration getBlackList();

    IConfiguration getDatabase();

    IAntiBotManager getAntiBotManager();

    LogHelper getLogHelper();

    Class<?> getClassInstance();

    UserDataService getUserDataService();

    ICore getCore();

    boolean isConnected(String ip);
}
