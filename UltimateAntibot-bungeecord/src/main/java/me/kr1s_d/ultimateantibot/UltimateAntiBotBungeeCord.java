package me.kr1s_d.ultimateantibot;


import me.kr1s_d.ultimateantibot.commands.CommandManager;
import me.kr1s_d.ultimateantibot.commands.subcommands.*;
import me.kr1s_d.ultimateantibot.common.*;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.ServerType;
import me.kr1s_d.ultimateantibot.common.objects.filter.LogFilterV2;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.thread.AttackAnalyzerThread;
import me.kr1s_d.ultimateantibot.common.thread.LatencyThread;
import me.kr1s_d.ultimateantibot.common.utils.*;
import me.kr1s_d.ultimateantibot.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.events.CustomEventListener;
import me.kr1s_d.ultimateantibot.events.HandShakeListener;
import me.kr1s_d.ultimateantibot.events.MainEventListener;
import me.kr1s_d.ultimateantibot.events.PingListener;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.ultimateantibot.utils.Metrics;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class UltimateAntiBotBungeeCord extends Plugin implements IAntiBotPlugin {
    private static UltimateAntiBotBungeeCord instance;

    private TaskScheduler scheduler;
    private IConfiguration config;
    private IConfiguration messages;
    private IConfiguration whitelist;
    private IConfiguration blacklist;
    private IConfiguration database;
    private IAntiBotManager antiBotManager;
    private LatencyThread latencyThread;
    private AnimationThread animationThread;
    private LogHelper logHelper;
    private UserDataService userDataService;
    private VPNService VPNService;
    private Notificator notificator;
    private CheckService checkService;
    private ICore core;
    //private SatelliteServer satelliteServer;
    private boolean isRunning;

    public void onEnable() {
        instance = this;
        this.isRunning = true;
        PerformanceHelper.init(ServerType.BUNGEECORD);
        long a = System.currentTimeMillis();
        this.scheduler = ProxyServer.getInstance().getScheduler();
        this.config = new Config(this, "%datafolder%/config.yml");
        this.messages = new Config(this, "%datafolder%/messages.yml");
        this.whitelist = new Config(this, "%datafolder%/whitelist.yml");
        this.blacklist = new Config(this, "%datafolder%/blacklist.yml");
        this.database = new Config(this, "%datafolder%/database.yml");
        FilesUpdater.checkFiles(this, 4.0D, this.config, this.messages);
        try {
            ConfigManger.init(this.config);
            MessageManager.init(this.messages);
        } catch (Exception e) {
            this.logHelper.error("Error during config.yml & messages.yml loading!");
        }
        Version.init(this);
        Metrics metrics = new Metrics(this, 11712);
        this.logHelper = new LogHelper(ProxyServer.getInstance().getLogger());
        this.logHelper.info("&fLoading &cUltimateAntiBot...");
        this.VPNService = new VPNService(this);
        this.VPNService.load();
        this.antiBotManager = new AntiBotManager(this);
        this.antiBotManager.getQueueService().load();
        this.antiBotManager.getWhitelistService().load();
        this.antiBotManager.getBlackListService().load();
        this.latencyThread = new LatencyThread(this);
        this.animationThread = new AnimationThread(this);
        this.core = new UltimateAntiBotCore(this);
        this.core.load();
        //this.satelliteServer = new SatelliteServer(this);
        this.userDataService = new UserDataService(this.database, this);
        this.userDataService.load();
        ProxyServer.getInstance().getLogger().setFilter(new LogFilterV2(this));
        this.notificator = new Notificator();
        this.notificator.init(this);
        this.checkService = new CheckService(this);
        this.checkService.load();
        new AttackAnalyzerThread(this);
        this.logHelper.info("&fLoaded &cUltimateAntiBot!");
        this.logHelper.sendLogo();
        this.logHelper.info("&cVersion: &f$1 &4| &cAuthor: &f$2 &4| &cCores: &f$3 &4| &cMode: $4"
                .replace("$1", getDescription().getVersion())
                .replace("$2", getDescription().getAuthor())
                .replace("$3", String.valueOf(Version.getCores()))
                .replace("$4", String.valueOf(PerformanceHelper.getPerformanceMode())));
        this.logHelper.info("&fThe &cabyss&f is ready to swallow all the bots!");
        CommandManager commandManager = new CommandManager(this, "ultimateantibot", "", "ab", "uab");
        commandManager.register(new AddRemoveBlacklistCommand(this));
        commandManager.register(new AddRemoveWhitelistCommand(this));
        commandManager.register(new ClearCommand(this));
        commandManager.register(new DumpCommand(this));
        commandManager.register(new HelpCommand(this));
        commandManager.register(new StatsCommand(this));
        commandManager.register(new ToggleNotificationCommand());
        commandManager.register(new CheckIDCommand(this));
        //commandManager.register(new SatelliteCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, commandManager);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MainEventListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new CustomEventListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new HandShakeListener(this));
        long b = System.currentTimeMillis() - a;
        this.logHelper.info("&7Took &c" + b + "ms&7 to load");
        new Updater(this);
    }

    public void onDisable() {
        long a = System.currentTimeMillis();
        this.logHelper.info("&cUnloading...");
        this.userDataService.unload();
        this.VPNService.unload();
        this.antiBotManager.getBlackListService().unload();
        this.antiBotManager.getWhitelistService().unload();
        this.logHelper.info("&cThanks for choosing us!");
        long b = System.currentTimeMillis() - a;
        this.isRunning = false;
        this.logHelper.info("&7Took &c" + b + "ms&7 to unload");
    }

    public void runTask(Runnable task, boolean isAsync) {
        if (isAsync) {
            this.scheduler.runAsync(this, task);
        } else {
            this.scheduler.schedule(this, task, 0L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void runTask(UABRunnable runnable) {
        runTask(runnable, runnable.isAsync());
    }


    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds) {
        if (async) {
            this.scheduler.schedule(this, () -> this.scheduler.runAsync(this, runnable), milliseconds, TimeUnit.MILLISECONDS);
        } else {
            this.scheduler.schedule(this, runnable, milliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void scheduleDelayedTask(UABRunnable runnable) {
        scheduleDelayedTask(runnable, runnable.isAsync(), runnable.getPeriod());
    }

    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        if (async) {
            this.scheduler.schedule(this, () -> this.scheduler.runAsync(this, runnable), 0L, repeatMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            this.scheduler.schedule(this, runnable, 0L, repeatMilliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void scheduleRepeatingTask(UABRunnable runnable) {
        scheduleRepeatingTask(runnable, runnable.isAsync(), runnable.getPeriod());
    }

    public IConfiguration getConfigYml() {
        return this.config;
    }

    public IConfiguration getMessages() {
        return this.messages;
    }

    public IConfiguration getWhitelist() {
        return this.whitelist;
    }

    public IConfiguration getBlackList() {
        return this.blacklist;
    }

    public IConfiguration getDatabase() {
        return this.database;
    }

    public IAntiBotManager getAntiBotManager() {
        return this.antiBotManager;
    }

    public LatencyThread getLatencyThread() {
        return this.latencyThread;
    }

    public AnimationThread getAnimationThread() {
        return this.animationThread;
    }

    public LogHelper getLogHelper() {
        return this.logHelper;
    }

    public Class<?> getClassInstance() {
        return ProxyServer.getInstance().getClass();
    }

    public UserDataService getUserDataService() {
        return this.userDataService;
    }

    public VPNService getVPNService() {
        return this.VPNService;
    }

    public INotificator getNotificator() {
        return this.notificator;
    }

    public CheckService getCheckService() {
        return this.checkService;
    }

    public ICore getCore() {
        return this.core;
    }

    public boolean isConnected(String ip) {
        List<String> ips = new ArrayList<>();
        ProxyServer.getInstance().getPlayers().forEach(a -> ips.add(Utils.getIP(a)));
        return ips.contains(ip);
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    public void disconnect(String ip, String reasonNoColor) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (Utils.getIP(player).equals(ip))
                player.disconnect(new TextComponent(Utils.colora(reasonNoColor)));
        }
    }

    //public SatelliteServer getSatellite() {
    //    return this.satelliteServer;
    //}

    public boolean isRunning() {
        return this.isRunning;
    }

    public static UltimateAntiBotBungeeCord getInstance() {
        return instance;
    }
}
