package me.kr1s_d.ultimateantibot;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import me.kr1s_d.ultimateantibot.commands.CommandWrapper;
import me.kr1s_d.ultimateantibot.commands.subcommands.*;
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
import me.kr1s_d.ultimateantibot.filter.Velocity247Filter;
import me.kr1s_d.ultimateantibot.filter.VelocityAttackFilter;
import me.kr1s_d.ultimateantibot.listener.CustomEventListener;
import me.kr1s_d.ultimateantibot.listener.MainEventListener;
import me.kr1s_d.ultimateantibot.listener.PingListener;
import me.kr1s_d.ultimateantibot.scheduler.TaskScheduler;
import me.kr1s_d.ultimateantibot.utils.ColorUtils;
import me.kr1s_d.ultimateantibot.utils.Config;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(id = "ultimateantibot-velocity", name = "UltimateAntiBotVelocity", version = "4.1.0-ABYSS", url = "ultimateantibot.kr1sd.me", description = "Just another antibot plugin :D", authors = {"Kr1S_D"})
public class UltimateAntiBotVelocity implements IAntiBotPlugin, IServerPlatform {
    private static final String AUTHOR = "Kr1S_D";
    private static final String VERSION = "4.1.0-ABYSS";
    private static UltimateAntiBotVelocity instance;

    //defaults
    private final ProxyServer server;
    private final Logger logger;
    private final Path directory;

    //uab
    private Scheduler scheduler;
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

    @Inject
    public UltimateAntiBotVelocity(ProxyServer server, Logger logger, @DataDirectory Path directory) {
        this.server = server;
        this.logger = logger;
        this.directory = directory;
        UltimateAntiBotVelocity.instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        this.isRunning = true;
        this.scheduler = server.getScheduler();
        PerformanceHelper.init(ServerType.VELOCITY);
        RuntimeUtil.setup(this);
        ServerUtil.setPlatform(this);
        long a = System.currentTimeMillis();
        this.config = new Config("%datafolder%/config.yml");
        this.messages = new Config("%datafolder%/messages.yml");
        this.whitelist = new Config("%datafolder%/whitelist.yml");
        this.blacklist = new Config("%datafolder%/blacklist.yml");
        this.logHelper = new LogHelper(this);
        FilesUpdater updater = new FilesUpdater(this, config, messages, whitelist, blacklist);
        updater.check(4.3, 4.3);
        if (updater.requiresReassign()) {
            this.config = new Config("%datafolder%/config.yml");
            this.messages = new Config("%datafolder%/messages.yml");
            this.whitelist = new Config("%datafolder%/whitelist.yml");
            this.blacklist = new Config("%datafolder%/blacklist.yml");
        }
        try {
            ConfigManger.init(this.config);
            MessageManager.init(this.messages);
        } catch (Exception e) {
            this.logHelper.error("Error during config.yml & messages.yml loading!");
            return;
        }
        Version.init(this);
        //new Metrics(this, 11712);
        this.logHelper.info("§fLoading &cUltimateAntiBot...");
        this.firewallService = new FirewallService(this);
        this.VPNService = new VPNService(this);
        this.VPNService.load();
        this.antiBotManager = new AntiBotManager(this);
        this.antiBotManager.getQueueService().load();
        this.antiBotManager.getWhitelistService().load();
        this.antiBotManager.getBlackListService().load();
        this.attackTrackerService = new AttackTrackerService(this);
        attackTrackerService.load();
        this.firewallService.enable();
        this.latencyThread = new LatencyThread(this);
        this.animationThread = new AnimationThread(this);
        this.core = new UltimateAntiBotCore(this);
        this.core.load();
        this.userDataService = new UserDataService(this);
        this.userDataService.load();
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new VelocityAttackFilter(this));
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new Velocity247Filter(this));
        satellite = new SatelliteServer(this);
        this.notificator = new Notificator();
        this.notificator.init(this);
        new AttackAnalyzerThread(this);
        this.logHelper.info("§fLoaded &cUltimateAntiBot!");
        this.logHelper.sendLogo();
        this.logHelper.info("§cVersion: §f$1 &4| §cAuthor: §f$2 §4| §cCores: §f$3 §4| §cMode: $4"
                .replace("$1", VERSION)
                .replace("$2", AUTHOR)
                .replace("$3", String.valueOf(Version.getCores()))
                .replace("$4", String.valueOf(PerformanceHelper.getPerformanceMode())));
        this.logHelper.info("§fThe §cabyss&f is ready to swallow all the bots!");
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("uab")
                .aliases("ultimateantibot")
                .plugin(this)
                .build();
        CommandWrapper wrapper = new CommandWrapper(this);
        wrapper.register(new AddRemoveBlacklistCommand(this));
        wrapper.register(new AddRemoveWhitelistCommand(this));
        wrapper.register(new ClearCommand(this));
        wrapper.register(new DumpCommand(this));
        wrapper.register(new HelpCommand(this));
        wrapper.register(new StatsCommand(this));
        wrapper.register(new ToggleNotificationCommand());
        wrapper.register(new CheckIDCommand(this));
        wrapper.register(new ReloadCommand(this));
        wrapper.register(new FirewallCommand(this));
        wrapper.register(new AttackLogCommand(this));
        wrapper.register(new CacheCommand());
        commandManager.register(commandMeta, wrapper);
        server.getEventManager().register(this, new PingListener(this));
        server.getEventManager().register(this, new MainEventListener(this));
        server.getEventManager().register(this, new CustomEventListener(this));
        //server.getEventManager().register(this, new HandShakeListener(this));
        long b = System.currentTimeMillis() - a;
        this.logHelper.info("&7Took &c" + b + "ms&7 to load");
        new Updater(this);
    }

    @Subscribe
    public void onProxyShutDownEvent(ProxyShutdownEvent event) {
        long a = System.currentTimeMillis();
        this.logHelper.info("&cUnloading...");
        this.isRunning = false;
        this.attackTrackerService.unload();
        this.firewallService.shutDownFirewall();
        this.userDataService.unload();
        this.VPNService.unload();
        this.antiBotManager.getBlackListService().unload();
        this.antiBotManager.getWhitelistService().unload();
        this.logHelper.info("&cThanks for choosing us!");
        long b = System.currentTimeMillis() - a;
        this.logHelper.info("&7Took &c" + b + "ms&7 to unload");
    }


    @Override
    public void reload() {
        this.config = new Config("%datafolder%/config.yml");
        this.messages = new Config("%datafolder%/messages.yml");

        ConfigManger.init(config);
        MessageManager.init(messages);
    }

    @Override
    public void runTask(Runnable task, boolean isAsync) {
        TaskScheduler.trackTask(scheduler.buildTask(this, task).schedule());
    }

    @Override
    public void runTask(UABRunnable runnable) {
        ScheduledTask task = scheduler.buildTask(this, runnable).schedule();
        runnable.setTaskID((int) TaskScheduler.trackTask(task));
    }

    @Override
    public void scheduleDelayedTask(Runnable runnable, boolean async, long milliseconds) {
        TaskScheduler.trackTask(scheduler.buildTask(this, runnable)
                .delay(milliseconds, TimeUnit.MILLISECONDS)
                .schedule());
    }

    @Override
    public void scheduleDelayedTask(UABRunnable runnable) {
        ScheduledTask task = scheduler.buildTask(this, runnable)
                .delay(runnable.getPeriod(), TimeUnit.MILLISECONDS)
                .schedule();

        runnable.setTaskID((int) TaskScheduler.trackTask(task));
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, boolean async, long repeatMilliseconds) {
        TaskScheduler.trackTask(scheduler.buildTask(this, runnable)
                .repeat(repeatMilliseconds, TimeUnit.MILLISECONDS)
                .schedule());
    }

    @Override
    public void scheduleRepeatingTask(UABRunnable runnable) {
        ScheduledTask task = scheduler.buildTask(this, runnable)
                .repeat(runnable.getPeriod(), TimeUnit.MILLISECONDS)
                .schedule();


        runnable.setTaskID((int) TaskScheduler.trackTask(task));
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
        return server.getClass();
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
        server.getAllPlayers().forEach(a -> ips.add(Utils.getIP(a)));
        return ips.contains(ip);
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void disconnect(String ip, String reasonNoColor) {
        for (Player player : server.getAllPlayers()) {
            if (Utils.getIP(player).equals(ip))
                player.disconnect(Utils.colora(reasonNoColor));
        }
    }

    @Override
    public int getOnlineCount() {
        return server.getPlayerCount();
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public String colorize(String text) {
        return ColorUtils.format(text, a -> a);
    }

    @Override
    public void cancelTask(int id) {
        TaskScheduler.cancelTrackedTask(id);
    }

    @Override
    public void log(LogHelper.LogType type, String log) {
        switch (type) {
            case ERROR:
                logger.error(log);
                break;
            case WARNING:
                logger.warn(log);
                break;
            case INFO:
                logger.info(log);
                break;
        }
    }

    @Override
    public void broadcast(String message) {
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(Utils.colora(message));
        }
    }

    @Override
    public AttackTrackerService getAttackTrackerService() {
        return attackTrackerService;
    }

    @Override
    public File getDFolder() {
        return directory.toFile();
    }

    public ProxyServer getServer() {
        return server;
    }

    public static UltimateAntiBotVelocity getInstance() {
        return instance;
    }
}
