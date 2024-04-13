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
import me.kr1s_d.ultimateantibot.utils.Metrics;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class UltimateAntiBotSpigot extends JavaPlugin implements IAntiBotPlugin, IServerPlatform {

    private static UltimateAntiBotSpigot instance;
    private BukkitScheduler scheduler;
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
    private SatelliteServer satellite;
    private boolean isRunning;

    @Override
    public void onEnable() {
        long a = System.currentTimeMillis();
        instance = this;
        this.isRunning = true;
        PerformanceHelper.init(ServerType.SPIGOT);
        ServerUtil.setInstance(this);
        this.scheduler = Bukkit.getScheduler();
        this.config = new Config(this, "config");
        this.messages = new Config(this, "messages");
        this.whitelist = new Config(this, "whitelist");
        this.blacklist = new Config(this, "blacklist");
        this.logHelper = new LogHelper(this);
        FilesUpdater updater = new FilesUpdater(this, config, messages, whitelist, blacklist);
        updater.check(4.4, 4.4);
        if (updater.requiresReassign()) {
            this.config = new Config(this, "config");
            this.messages = new Config(this, "messages");
            this.whitelist = new Config(this, "whitelist");
            this.blacklist = new Config(this, "blacklist");
        }
        try {
            ConfigManger.init(config, false);
            PerformanceHelper.init(ServerType.SPIGOT);
            MessageManager.init(messages);
        } catch (Exception e) {
            logHelper.error("[ERROR] Error during config.yml & messages.yml loading!");
            e.printStackTrace();
            throw e;
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
        userDataService = new UserDataService(this);
        userDataService.load();
        this.core = new UltimateAntiBotCore(this);
        this.core.load();
        ((Logger) LogManager.getRootLogger()).addFilter(new BukkitAttackFilter(this));
        ((Logger) LogManager.getRootLogger()).addFilter(new Bukkit247Filter(this));
        satellite = new SatelliteServer(this);
        notificator = new Notificator();
        notificator.init(this);
        new AttackAnalyzerThread(this);
        logHelper.info("&fLoaded &cUltimateAntiBot!");
        logHelper.sendLogo();
        logHelper.info("&cVersion: &f$1 &4| &cAuthor: &f$2 &4| &cCores: &f$3 &4| &cMode: &f$4"
                .replace("$1", this.getDescription().getVersion())
                .replace("$2", this.getDescription().getAuthors().toString())
                .replace("$3", String.valueOf(PerformanceHelper.getCores()))
                .replace("$4", String.valueOf(PerformanceHelper.get()))
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
        commandManager.register(new ConnectionProfileCommand(this));
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

        ConfigManger.init(config, true);
        MessageManager.init(messages);
    }

    @Override
    public void runTask(Runnable task, boolean isAsync) {
        if (isAsync) {
            scheduler.runTaskAsynchronously(this, task);
        } else {
            scheduler.runTask(this, task);
        }
    }

    @Override
    public void runTask(UABRunnable runnable) {
        BukkitTask bukkitTask = null;

        if (runnable.isAsync()) {
            bukkitTask = scheduler.runTaskAsynchronously(this, runnable);
        } else {
            bukkitTask = scheduler.runTask(this, runnable);
        }

        runnable.setTaskID(bukkitTask.getTaskId());
    }

    @Override
    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds) {
        if (async) {
            scheduler.runTaskLaterAsynchronously(this, runnable, Utils.convertToTicks(milliseconds));
        } else {
            scheduler.runTaskLater(this, runnable, Utils.convertToTicks(milliseconds));
        }
    }

    @Override
    public void scheduleDelayedTask(UABRunnable runnable) {
        BukkitTask bukkitTask = null;

        if (runnable.isAsync()) {
            bukkitTask = scheduler.runTaskLaterAsynchronously(this, runnable, Utils.convertToTicks(runnable.getPeriod()));
        } else {
            bukkitTask = scheduler.runTaskLater(this, runnable, Utils.convertToTicks(runnable.getPeriod()));
        }

        runnable.setTaskID(bukkitTask.getTaskId());
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        if (async) {
            scheduler.runTaskTimerAsynchronously(this, runnable, 0, Utils.convertToTicks(repeatMilliseconds));
        } else {
            scheduler.runTaskTimer(this, runnable, 0, Utils.convertToTicks(repeatMilliseconds));
        }
    }

    @Override
    public void scheduleRepeatingTask(UABRunnable runnable) {
        BukkitTask bukkitTask = null;

        if (runnable.isAsync()) {
            bukkitTask = scheduler.runTaskTimerAsynchronously(this, runnable, 0, Utils.convertToTicks(runnable.getPeriod()));
        } else {
            bukkitTask = scheduler.runTaskTimer(this, runnable, 0, Utils.convertToTicks(runnable.getPeriod()));
        }

        runnable.setTaskID(bukkitTask.getTaskId());
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
        Bukkit.getScheduler().cancelTask(id);
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
