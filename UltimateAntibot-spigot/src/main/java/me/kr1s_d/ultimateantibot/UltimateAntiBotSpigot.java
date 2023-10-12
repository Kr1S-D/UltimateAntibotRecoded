package me.kr1s_d.ultimateantibot;

import me.kr1s_d.commandframework.CommandManager;
import me.kr1s_d.ultimateantibot.commands.*;
import me.kr1s_d.ultimateantibot.common.*;
import me.kr1s_d.ultimateantibot.common.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.common.core.server.SatelliteServer;
import me.kr1s_d.ultimateantibot.common.core.thread.AnimationThread;
import me.kr1s_d.ultimateantibot.common.core.thread.AttackAnalyzerThread;
import me.kr1s_d.ultimateantibot.common.core.thread.LatencyThread;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.ServerType;
import me.kr1s_d.ultimateantibot.common.service.AttackTrackerService;
import me.kr1s_d.ultimateantibot.common.service.FirewallService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.utils.*;
import me.kr1s_d.ultimateantibot.listener.CustomEventListener;
import me.kr1s_d.ultimateantibot.listener.MainEventListener;
import me.kr1s_d.ultimateantibot.listener.PingListener;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.ultimateantibot.objects.filter.Bukkit247Filter;
import me.kr1s_d.ultimateantibot.objects.filter.BukkitAttackFilter;
import me.kr1s_d.ultimateantibot.utils.BukkitWithFoliaScheduler;
import me.kr1s_d.ultimateantibot.utils.Metrics;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class UltimateAntiBotSpigot extends JavaPlugin implements IAntiBotPlugin, IServerPlatform {

    private static UltimateAntiBotSpigot instance;
    private BukkitWithFoliaScheduler betascheduler;
    private IConfiguration config;
    private IConfiguration messages;
    private IConfiguration whitelist;
    private IConfiguration blacklist;
    private IAntiBotManager antiBotManager;
    private LatencyThread latencyThread;
    private AnimationThread animationThread;
    private LogHelper logHelper;
    private FirewallService firewallService;
    private UserDataService userDataService;
    private AttackTrackerService attackTrackerService;
    private VPNService VPNService;
    private Notificator notificator;
    private UltimateAntiBotCore core;
    @SuppressWarnings("unused")
    private SatelliteServer satellite;
    private boolean isRunning;

    @Override
    public void onEnable() {
        long a = System.currentTimeMillis();
        instance = this;
        this.isRunning = true;
        PerformanceHelper.init(ServerType.SPIGOT);
        RuntimeUtil.setup(this);
        ServerUtil.setPlatform(this);
        this.betascheduler = new BukkitWithFoliaScheduler(this);
        this.config = new Config(this, "config");
        this.messages = new Config(this, "messages");
        this.whitelist = new Config(this, "whitelist");
        this.blacklist = new Config(this, "blacklist");
        this.logHelper = new LogHelper(this);
        FilesUpdater updater = new FilesUpdater(this, config, messages, whitelist, blacklist);
        updater.check(4.3, 4.3);
        if (updater.requiresReassign()) {
            this.config = new Config(this, "config");
            this.messages = new Config(this, "messages");
            this.whitelist = new Config(this, "whitelist");
            this.blacklist = new Config(this, "blacklist");
        }
        try {
            ConfigManger.init(config);
            MessageManager.init(messages);
        } catch (Exception e) {
            logHelper.error("Error during config.yml & messages.yml loading!");
            return;
        }
        Version.init(this);
        new Metrics(this, 11777);
        logHelper.info("&fLoading &cUltimateAntiBot...");
        this.firewallService = new FirewallService(this);
        VPNService = new VPNService(this);
        VPNService.load();
        antiBotManager = new AntiBotManager(this);
        antiBotManager.getQueueService().load();
        antiBotManager.getWhitelistService().load();
        antiBotManager.getBlackListService().load();
        this.attackTrackerService = new AttackTrackerService(this);
        attackTrackerService.load();
        firewallService.enable();
        latencyThread = new LatencyThread(this);
        animationThread = new AnimationThread(this);
        core = new UltimateAntiBotCore(this);
        core.load();
        userDataService = new UserDataService(this);
        userDataService.load();
        ((Logger) LogManager.getRootLogger()).addFilter(new BukkitAttackFilter(this));
        ((Logger) LogManager.getRootLogger()).addFilter(new Bukkit247Filter(this));
        satellite = new SatelliteServer(this);
        notificator = new Notificator();
        notificator.init(this);
        new AttackAnalyzerThread(this);
        logHelper.info("&fLoaded &cUltimateAntiBot!");
        logHelper.sendLogo();
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
        commandManager.register(new ReloadCommand(this));
        commandManager.register(new FirewallCommand(this));
        commandManager.register(new AttackLogCommand(this));
        commandManager.register(new CacheCommand());
        commandManager.setWrongArgumentMessage(MessageManager.commandWrongArgument);
        commandManager.setNoPlayerMessage("&fYou are not a &cplayer!");
        //commandManager.register(new SatelliteCommand(this));
        Bukkit.getPluginManager().registerEvents(new PingListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MainEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomEventListener(this), this);
        long b = System.currentTimeMillis() - a;
        logHelper.info("&7Took &c" + b + "ms&7 to load");
        new Updater(this);
    }

    @Override
    public void onDisable() {
        long a = System.currentTimeMillis();
        logHelper.info("&cUnloading...");
        this.isRunning = false;
        this.attackTrackerService.unload();
        firewallService.shutDownFirewall();
        userDataService.unload();
        VPNService.unload();
        antiBotManager.getBlackListService().unload();
        antiBotManager.getWhitelistService().unload();
        logHelper.info("&cThanks for choosing us!");
        long b = System.currentTimeMillis() - a;
        logHelper.info("&7Took &c" + b + "ms&7 to unload");
    }

    @Override
    public void reload() {
        this.config = new Config(this, "config");
        this.messages = new Config(this, "messages");

        ConfigManger.init(config);
        MessageManager.init(messages);
    }

    @Override
    public void runTask(Runnable task, boolean isAsync) {
        betascheduler.run(task, isAsync);
    }

    @Override
    public void runTask(UABRunnable runnable) {
        runnable.setTaskID(betascheduler.run(runnable, runnable.isAsync()));
    }

    @Override
    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds) {
        betascheduler.scheduleDelayed(runnable, async, milliseconds, true);
    }

    @Override
    public void scheduleDelayedTask(UABRunnable runnable) {
        runnable.setTaskID(betascheduler.scheduleDelayed(runnable, runnable.isAsync(), runnable.getPeriod(), true));
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        betascheduler.scheduleRepeat(runnable, async, 0, repeatMilliseconds, true);
    }

    @Override
    public void scheduleRepeatingTask(UABRunnable runnable) {
        runnable.setTaskID(betascheduler.scheduleRepeat(runnable, runnable.isAsync(), 0,  runnable.getPeriod(), true));
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
    public VPNService getVPNService() {
        return VPNService;
    }

    @Override
    public INotificator getNotificator() {
        return notificator;
    }

    @Override
    public UltimateAntiBotCore getCore() {
        return core;
    }

    @Override
    public FirewallService getFirewallService() {
        return firewallService;
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
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Utils.getPlayerIP(player).equals(ip)) {
                        player.kickPlayer(Utils.colora(reasonNoColor));
                    }
                }
            }
        }.runTaskLater(this, 1);
    }

    @Override
    public int getOnlineCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public void cancelTask(int id) {
        betascheduler.cancel(id);
    }

    @Override
    public void log(LogHelper.LogType type, String log) {
        Bukkit.getConsoleSender().sendMessage(log);
    }

    @Override
    public void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Utils.colora(message));
        }
    }

    @Override
    public AttackTrackerService getAttackTrackerService(){
        return attackTrackerService;
    }

    @Override
    public File getDFolder() {
        return getDataFolder();
    }

    public static UltimateAntiBotSpigot getInstance() {
        return instance;
    }
}
