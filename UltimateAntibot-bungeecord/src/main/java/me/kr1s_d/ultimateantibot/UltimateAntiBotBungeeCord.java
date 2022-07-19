package me.kr1s_d.ultimateantibot;


import me.kr1s_d.ultimateantibot.commands.CommandManager;
import me.kr1s_d.ultimateantibot.commands.subcommands.*;
import me.kr1s_d.ultimateantibot.common.*;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.ServerType;
import me.kr1s_d.ultimateantibot.common.objects.filter.LogFilterV2;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.service.FirewallService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.thread.AttackAnalyzerThread;
import me.kr1s_d.ultimateantibot.common.thread.LatencyThread;
import me.kr1s_d.ultimateantibot.common.utils.*;
import me.kr1s_d.ultimateantibot.common.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.listener.CustomEventListener;
import me.kr1s_d.ultimateantibot.listener.HandShakeListener;
import me.kr1s_d.ultimateantibot.listener.MainEventListener;
import me.kr1s_d.ultimateantibot.listener.PingListener;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.ultimateantibot.utils.Metrics;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class UltimateAntiBotBungeeCord extends Plugin implements IAntiBotPlugin, IServerPlatform {
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
    private FirewallService firewallService;
    private UserDataService userDataService;
    private VPNService VPNService;
    private Notificator notificator;
    private CheckService checkService;
    private UltimateAntiBotCore core;
    //private SatelliteServer satelliteServer;
    private boolean isRunning;

    public void onEnable() {
        instance = this;
        this.isRunning = true;
        PerformanceHelper.init(ServerType.BUNGEECORD);
        RuntimeUtil.setup(this);
        ServerUtil.setPlatform(this);
        long a = System.currentTimeMillis();
        this.scheduler = ProxyServer.getInstance().getScheduler();
        this.config = new Config(this, "%datafolder%/config.yml");
        this.messages = new Config(this, "%datafolder%/messages.yml");
        this.whitelist = new Config(this, "%datafolder%/whitelist.yml");
        this.blacklist = new Config(this, "%datafolder%/blacklist.yml");
        this.database = new Config(this, "%datafolder%/database.yml");
        if (FilesUpdater.checkFiles(this, 4.0, this.config, this.messages)) {
            logHelper.error("Unable to load UltimateAntiBot....");
            logHelper.error("Please remove your files!");
            logHelper.error("Unloading plugin...");
            return;
        }
        try {
            ConfigManger.init(this.config);
            MessageManager.init(this.messages);
        } catch (Exception e) {
            this.logHelper.error("Error during config.yml & messages.yml loading!");
            return;
        }
        Version.init(this);
        new Metrics(this, 11712);
        this.logHelper = new LogHelper(ProxyServer.getInstance().getLogger());
        this.logHelper.info("&fLoading &cUltimateAntiBot...");
        this.firewallService = new FirewallService(this);
        this.VPNService = new VPNService(this);
        this.VPNService.load();
        this.antiBotManager = new AntiBotManager(this);
        this.antiBotManager.getQueueService().load();
        this.antiBotManager.getWhitelistService().load();
        this.antiBotManager.getBlackListService().load();
        this.firewallService.enable();
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
        commandManager.register(new ReloadCommand(this));
        commandManager.register(new FirewallCommand(this));
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
        this.firewallService.shutDownFirewall();
        this.userDataService.unload();
        this.VPNService.unload();
        this.antiBotManager.getBlackListService().unload();
        this.antiBotManager.getWhitelistService().unload();
        this.logHelper.info("&cThanks for choosing us!");
        long b = System.currentTimeMillis() - a;
        this.isRunning = false;
        this.logHelper.info("&7Took &c" + b + "ms&7 to unload");
    }

    @Override
    public void reload() {
        this.config = new Config(this, "%datafolder%/config.yml");
        this.messages = new Config(this, "%datafolder%/messages.yml");

        ConfigManger.init(config);
        MessageManager.init(messages);
    }

    @Override
    public void runTask(Runnable task, boolean isAsync) {
        if (isAsync) {
            this.scheduler.runAsync(this, task);
        } else {
            this.scheduler.schedule(this, task, 0L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void runTask(UABRunnable runnable) {
        ScheduledTask task = null;

        if (runnable.isAsync()) {
            task = this.scheduler.runAsync(this, runnable);
        } else {
            task = this.scheduler.schedule(this, runnable, 0L, TimeUnit.SECONDS);
        }

        runnable.setTaskID(task.getId());
    }

    @Override
    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds) {
        if (async) {
            this.scheduler.schedule(this, () -> this.scheduler.runAsync(this, runnable), milliseconds, TimeUnit.MILLISECONDS);
        } else {
            this.scheduler.schedule(this, runnable, milliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void scheduleDelayedTask(UABRunnable runnable) {
        ScheduledTask task = null;

        if (runnable.isAsync()) {
            task = this.scheduler.schedule(this, () -> this.scheduler.runAsync(this, runnable), runnable.getTaskID(), TimeUnit.MILLISECONDS);
        } else {
            task = this.scheduler.schedule(this, runnable, runnable.getPeriod(), TimeUnit.MILLISECONDS);
        }

        runnable.setTaskID(task.getId());
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        if (async) {
            this.scheduler.schedule(this, () -> this.scheduler.runAsync(this, runnable), 0L, repeatMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            this.scheduler.schedule(this, runnable, 0L, repeatMilliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void scheduleRepeatingTask(UABRunnable runnable) {
        ScheduledTask task = null;

        if (runnable.isAsync()) {
            task = this.scheduler.schedule(this, () -> this.scheduler.runAsync(this, runnable), 0L, runnable.getPeriod(), TimeUnit.MILLISECONDS);
        } else {
            task = this.scheduler.schedule(this, runnable, 0L, runnable.getPeriod(), TimeUnit.MILLISECONDS);
        }

        runnable.setTaskID(task.getId());
    }

    @Override
    public IConfiguration getConfigYml() {
        return this.config;
    }

    @Override
    public IConfiguration getMessages() {
        return this.messages;
    }

    @Override
    public IConfiguration getWhitelist() {
        return this.whitelist;
    }

    @Override
    public IConfiguration getBlackList() {
        return this.blacklist;
    }

    @Override
    public IConfiguration getDatabase() {
        return this.database;
    }

    @Override
    public IAntiBotManager getAntiBotManager() {
        return this.antiBotManager;
    }

    @Override
    public LatencyThread getLatencyThread() {
        return this.latencyThread;
    }

    @Override
    public AnimationThread getAnimationThread() {
        return this.animationThread;
    }

    @Override
    public LogHelper getLogHelper() {
        return this.logHelper;
    }

    @Override
    public Class<?> getClassInstance() {
        return ProxyServer.getInstance().getClass();
    }

    @Override
    public UserDataService getUserDataService() {
        return this.userDataService;
    }

    @Override
    public VPNService getVPNService() {
        return this.VPNService;
    }

    @Override
    public INotificator getNotificator() {
        return this.notificator;
    }

    @Override
    public CheckService getCheckService() {
        return this.checkService;
    }

    @Override
    public UltimateAntiBotCore getCore() {
        return this.core;
    }

    @Override
    public FirewallService getFirewallService() {
        return firewallService;
    }

    @Override
    public boolean isConnected(String ip) {
        List<String> ips = new ArrayList<>();
        ProxyServer.getInstance().getPlayers().forEach(a -> ips.add(Utils.getIP(a)));
        return ips.contains(ip);
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public void disconnect(String ip, String reasonNoColor) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (Utils.getIP(player).equals(ip))
                player.disconnect(new TextComponent(Utils.colora(reasonNoColor)));
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void cancelTask(int id) {
        ProxyServer.getInstance().getScheduler().cancel(id);
    }

    public static UltimateAntiBotBungeeCord getInstance() {
        return instance;
    }
}
