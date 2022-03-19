package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.commands.CommandManager;
import me.kr1s_d.ultimateantibot.commands.subcommands.*;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.enums.ServerType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.filter.LogFilter;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.*;
import me.kr1s_d.ultimateantibot.common.objects.server.SatelliteServer;
import me.kr1s_d.ultimateantibot.common.service.ConnectionCheckerService;
import me.kr1s_d.ultimateantibot.common.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.thread.AttackAnalyzerThread;
import me.kr1s_d.ultimateantibot.common.thread.LatencyThread;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.Version;
import me.kr1s_d.ultimateantibot.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.events.CustomEventListener;
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
    private ConnectionCheckerService connectionCheckerService;
    private Notificator notificator;
    private ICore core;
    private SatelliteServer satelliteServer;
    private boolean isRunning;

    @Override
    public void onEnable() {
        instance = this;
        this.isRunning = true;
        long a = System.currentTimeMillis();
        this.scheduler = ProxyServer.getInstance().getScheduler();
        this.config = new Config(this, "%datafolder%/config.yml");
        this.messages = new Config(this, "%datafolder%/messages.yml");
        this.whitelist = new Config(this, "%datafolder%/whitelist.yml");
        this.blacklist = new Config(this, "%datafolder%/blacklist.yml");
        this.database = new Config(this, "%datafolder%/database.yml");
        ConfigManger.init(config);
        MessageManager.init(messages);
        Version.init(this);
        new Metrics(this, 11712);
        logHelper = new LogHelper(ProxyServer.getInstance().getLogger());
        logHelper.info("&fLoading &cUltimateAntiBot...");
        connectionCheckerService = new ConnectionCheckerService(this);
        connectionCheckerService.load();
        antiBotManager = new AntiBotManager(this);
        antiBotManager.getQueueService().load();
        antiBotManager.getWhitelistService().load();
        antiBotManager.getBlackListService().load();
        latencyThread = new LatencyThread(this);
        animationThread = new AnimationThread(this);
        core = new UltimateAntiBotCore(this);
        core.load();
        satelliteServer = new SatelliteServer(this);
        userDataService = new UserDataService(database, this);
        userDataService.load();
        ProxyServer.getInstance().getLogger().setFilter(new LogFilter(this));
        notificator = new Notificator();
        notificator.init(this);
        new AttackAnalyzerThread(this);
        logHelper.info("&fLoaded &cUltimateAntiBot!");
        logHelper.sendLogo();
        PerformanceHelper.init(ServerType.BUNGEECORD);
        logHelper.info("&cVersion: &f$1 &4| &cAuthor: &f$2 &4| &cCores: &f$3 &4| &cMode: $4"
                .replace("$1", this.getDescription().getVersion())
                .replace("$2", this.getDescription().getAuthor())
                .replace("$3", String.valueOf(Version.getCores()))
                .replace("$4", String.valueOf(PerformanceHelper.getPerformanceMode()))
        );
        logHelper.info("&fThe &cabyss&f is ready to swallow all the bots!");
        CommandManager commandManager = new CommandManager(this, "uab", "", "ab");
        commandManager.register(new AddRemoveBlacklistCommand(this));
        commandManager.register(new AddRemoveWhitelistCommand(this));
        commandManager.register(new ClearCommand(this));
        commandManager.register(new DumpCommand(this));
        commandManager.register(new HelpCommand(this));
        commandManager.register(new StatsCommand(this));
        commandManager.register(new ToggleNotificationCommand());
        commandManager.register(new CheckIDCommand(this));
        commandManager.register(new SatelliteCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, commandManager);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MainEventListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new CustomEventListener());
        long b = System.currentTimeMillis() - a;
        logHelper.info("&7Took &c" + b + "ms&7 to load");
    }

    @Override
    public void onDisable() {
        long a = System.currentTimeMillis();
        logHelper.info("&cUnloading...");
        userDataService.unload();
        connectionCheckerService.unload();
        antiBotManager.getBlackListService().unload();
        antiBotManager.getWhitelistService().unload();
        logHelper.info("&cThanks for choosing us!");
        long b = System.currentTimeMillis() - a;
        this.isRunning = false;
        logHelper.info("&7Took &c" + b + "ms&7 to unload");
    }

    @Override
    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds){
        if(async){
            scheduler.schedule(this, ()-> scheduler.runAsync(this, runnable), milliseconds, TimeUnit.MILLISECONDS);
        }else {
            scheduler.schedule(this, runnable, milliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void runTask(Runnable task, boolean isAsync) {
        if(isAsync){
            scheduler.runAsync(this, task);
        }else{
            scheduler.schedule(this, task, 0, TimeUnit.SECONDS);
        }
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        if(async){
            scheduler.schedule(this, ()-> scheduler.runAsync(this, runnable), 0, repeatMilliseconds, TimeUnit.MILLISECONDS);
        }else {
            scheduler.schedule(this, runnable, 0, repeatMilliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public IConfiguration getConfig() {
        return config;
    }

    @Override
    public IConfiguration getMessages() {
        return messages;
    }

    @Override
    public IConfiguration getWhitelist() {
        return whitelist;
    }

    @Override
    public IConfiguration getBlackList() {
        return blacklist;
    }

    @Override
    public IConfiguration getDatabase() {
        return database;
    }

    @Override
    public IAntiBotManager getAntiBotManager() {
        return antiBotManager;
    }

    @Override
    public LatencyThread getLatencyThread() {
        return latencyThread;
    }

    @Override
    public AnimationThread getAnimationThread() {
        return animationThread;
    }

    @Override
    public LogHelper getLogHelper() {
        return logHelper;
    }

    @Override
    public Class<?> getClassInstance() {
        return ProxyServer.getInstance().getClass();
    }

    @Override
    public UserDataService getUserDataService() {
        return userDataService;
    }

    @Override
    public ConnectionCheckerService getConnectionCheckerService() {
        return connectionCheckerService;
    }

    @Override
    public INotificator getNotificator() {
        return notificator;
    }

    @Override
    public ICore getCore() {
        return core;
    }

    @Override
    public boolean isConnected(String ip) {
        List<String> ips = new ArrayList<>();
        ProxyServer.getInstance().getPlayers().forEach(a -> ips.add(Utils.getIP(a)));
        return ips.contains(ip);
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public void disconnect(String ip, String reasonNoColor) {
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
            if(Utils.getIP(player).equals(ip)){
                player.disconnect(new TextComponent(Utils.colora(reasonNoColor)));
            }
        }
    }

    @Override
    public SatelliteServer getSatellite() {
        return satelliteServer;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public static UltimateAntiBotBungeeCord getInstance() {
        return instance;
    }
}
