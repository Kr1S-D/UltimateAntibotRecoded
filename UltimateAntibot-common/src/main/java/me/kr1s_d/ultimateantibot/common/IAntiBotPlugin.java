package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.service.*;
import me.kr1s_d.ultimateantibot.common.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.thread.LatencyThread;

public interface IAntiBotPlugin {
    void reload();

    void runTask(Runnable task, boolean isAsync);

    void runTask(UABRunnable runnable);

    void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds);

    void scheduleDelayedTask(UABRunnable runnable);

    void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds);

    void scheduleRepeatingTask(UABRunnable runnable);

    IConfiguration getConfigYml();

    IConfiguration getMessages();

    IConfiguration getWhitelist();

    IConfiguration getBlackList();

    IAntiBotManager getAntiBotManager();

    LatencyThread getLatencyThread();

    AnimationThread getAnimationThread();

    LogHelper getLogHelper();

    Class<?> getClassInstance();

    UserDataService getUserDataService();

    VPNService getVPNService();

    INotificator getNotificator();

    UltimateAntiBotCore getCore();

    FirewallService getFirewallService();

    boolean isConnected(String ip);

    String getVersion();

    void disconnect(String ip, String reasonNoColor);

    int getOnlineCount();

    boolean isRunning();

    AttackTrackerService getAttackTrackerService();
}
