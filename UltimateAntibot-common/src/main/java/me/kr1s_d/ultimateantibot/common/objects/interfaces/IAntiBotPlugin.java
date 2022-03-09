package me.kr1s_d.ultimateantibot.common.objects.interfaces;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.server.SatelliteServer;
import me.kr1s_d.ultimateantibot.common.service.ConnectionCheckerService;
import me.kr1s_d.ultimateantibot.common.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.thread.LatencyThread;
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

    LatencyThread getLatencyThread();

    AnimationThread getAnimationThread();

    LogHelper getLogHelper();

    Class<?> getClassInstance();

    UserDataService getUserDataService();

    ConnectionCheckerService getConnectionCheckerService();

    INotificator getNotificator();

    ICore getCore();

    boolean isConnected(String ip);

    String getVersion();

    void disconnect(String ip, String reasonNoColor);

    SatelliteServer getSatellite();

    boolean isRunning();
}
