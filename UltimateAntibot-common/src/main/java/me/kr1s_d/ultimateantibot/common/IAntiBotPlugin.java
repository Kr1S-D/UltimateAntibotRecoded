package me.kr1s_d.ultimateantibot.common;

import me.kr1s_d.ultimateantibot.common.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.common.core.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.core.thread.LatencyThread;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.service.AttackTrackerService;
import me.kr1s_d.ultimateantibot.common.service.FirewallService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;

public interface IAntiBotPlugin extends IServerPlatform {
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
