package me.kr1s_d.ultimateantibot;

import me.kr1s_d.commandframework.CommandManager;
import me.kr1s_d.ultimateantibot.commands.*;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.enums.ServerType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.*;
import me.kr1s_d.ultimateantibot.common.utils.*;
import me.kr1s_d.ultimateantibot.objects.filter.BukkitFilter;
import me.kr1s_d.ultimateantibot.common.objects.server.SatelliteServer;
import me.kr1s_d.ultimateantibot.common.service.ConnectionCheckerService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.thread.AttackAnalyzerThread;
import me.kr1s_d.ultimateantibot.common.thread.LatencyThread;
import me.kr1s_d.ultimateantibot.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.events.CustomEventListener;
import me.kr1s_d.ultimateantibot.events.MainEventListener;
import me.kr1s_d.ultimateantibot.events.PingListener;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.ultimateantibot.utils.Metrics;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class UltimateAntiBotSpigot extends JavaPlugin implements IAntiBotPlugin {

    private static UltimateAntiBotSpigot instance;
    private BukkitScheduler scheduler;
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
        long a = System.currentTimeMillis();
        this.scheduler = Bukkit.getScheduler();
        this.config = new Config(this, "config");
        this.messages = new Config(this, "messages");
        this.whitelist = new Config(this, "whitelist");
        this.blacklist = new Config(this, "blacklist");
        this.database = new Config(this, "database");
        FilesUpdater.checkFiles(this, 4.0, config, messages);
        new Updater(this);
        try {
            ConfigManger.init(config);
            MessageManager.init(messages);
        }catch (Exception e){
            logHelper.error("Error during config.yml & messages.yml loading!");
        }
        Version.init(this);
        Metrics metrics = new Metrics(this, 11777);
        logHelper = new LogHelper(Bukkit.getLogger());
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
        ((Logger) LogManager.getRootLogger()).addFilter(new BukkitFilter(this));
        notificator = new Notificator();
        notificator.init(this);
        new AttackAnalyzerThread(this);
        logHelper.info("&fLoaded &cUltimateAntiBot!");
        logHelper.sendLogo();
        PerformanceHelper.init(ServerType.SPIGOT);
        logHelper.info("&cVersion: &f$1 &4| &cAuthor: &f$2 &4| &cCores: &f$3 &4| &cMode: $4"
                .replace("$1", this.getDescription().getVersion())
                .replace("$2", this.getDescription().getAuthors().toString())
                .replace("$3", String.valueOf(Version.getCores()))
                .replace("$4", String.valueOf(PerformanceHelper.getPerformanceMode()))
        );
        logHelper.info("&fThe &cabyss&f is ready to swallow all the bots!");
        CommandManager commandManager = new CommandManager("ultimateantibot", "", "ab", "uab");
        commandManager.setPrefix(MessageManager.prefix);
        commandManager.register(new AddRemoveBlacklistCommand(this));
        commandManager.register(new AddRemoveWhitelistCommand(this));
        commandManager.register(new ClearCommand(this));
        commandManager.register(new DumpCommand(this));
        commandManager.register(new HelpCommand(this));
        commandManager.register(new StatsCommand(this));
        commandManager.register(new ToggleNotificationCommand());
        commandManager.register(new CheckIDCommand(this));
        commandManager.register(new SatelliteCommand(this));
        Bukkit.getPluginManager().registerEvents(new PingListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MainEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomEventListener(), this);
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
    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds) {
        if(async){
            scheduler.runTaskLaterAsynchronously(this, runnable, Utils.convertToTicks(milliseconds));
        }else{
            scheduler.runTaskLater(this, runnable, Utils.convertToTicks(milliseconds));
        }
    }

    @Override
    public void runTask(Runnable task, boolean isAsync) {
        if(isAsync){
            scheduler.runTaskAsynchronously(this, task);
        }else{
            scheduler.runTask(this, task);
        }
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        if(async){
            scheduler.runTaskTimerAsynchronously(this, runnable, 0, Utils.convertToTicks(repeatMilliseconds));
        }else{
            scheduler.runTaskTimer(this, runnable, 0, Utils.convertToTicks(repeatMilliseconds));
        }
    }

    @Override
    public IConfiguration getConfigYml() {
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
        return Bukkit.spigot().getClass();
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
        return Bukkit.getOnlinePlayers().stream().anyMatch(p -> Utils.getPlayerIP(p).equals(ip));
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public void disconnect(String ip, String reasonNoColor) {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(Utils.getPlayerIP(player).equals(ip)){
                player.kickPlayer(Utils.colora(reasonNoColor));
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

    public static UltimateAntiBotSpigot getInstance() {
        return instance;
    }
}
