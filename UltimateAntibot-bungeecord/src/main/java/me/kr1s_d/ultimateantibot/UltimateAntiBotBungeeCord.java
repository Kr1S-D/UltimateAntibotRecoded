package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.commands.CommandManager;
import me.kr1s_d.ultimateantibot.commands.subcommands.*;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.filter.LogFilter;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.Version;
import me.kr1s_d.ultimateantibot.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.events.MainEventListener;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.utils.NotificationUtils;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class UltimateAntiBotBungeeCord extends Plugin implements IAntiBotPlugin {

    private TaskScheduler scheduler;
    private IConfiguration config;
    private IConfiguration messages;
    private IConfiguration whitelist;
    private IConfiguration blacklist;
    private IConfiguration database;
    private IAntiBotManager antiBotManager;
    private LogHelper logHelper;
    private UserDataService userDataService;
    private ICore core;

    @Override
    public void onEnable() {
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
        logHelper = new LogHelper(ProxyServer.getInstance().getLogger());
        logHelper.info("&fLoading &dUltimateAntiBot...");
        antiBotManager = new AntiBotManager(this);
        core = new UltimateAntiBotCore(this);
        core.load();
        antiBotManager.getQueueService().load();
        antiBotManager.getWhitelistService().load();
        antiBotManager.getBlackListService().load();
        userDataService = new UserDataService(database, this);
        userDataService.load();
        ProxyServer.getInstance().getLogger().setFilter(new LogFilter(antiBotManager));
        NotificationUtils.update(this);
        logHelper.info("&fLoaded &dUltimateAntiBot!");
        logHelper.sendLogo();
        logHelper.info("&dVersion: &f$1 &5| &dAuthor: &f$2 &5| &dCores: &f$3"
                .replace("$1", this.getDescription().getVersion())
                .replace("$2", this.getDescription().getAuthor())
                .replace("$3", String.valueOf(Version.getCores()))
        );
        logHelper.info("&fThe &dabyss&f is ready to swallow all the bots!");
        CommandManager commandManager = new CommandManager(this, "uab", "", "ab");
        commandManager.register(new AddRemoveBlacklistCommand(this));
        commandManager.register(new AddRemoveWhitelistCommand(this));
        commandManager.register(new ClearCommand(this));
        commandManager.register(new DumpCommand(this));
        commandManager.register(new HelpCommand(this));
        commandManager.register(new StatsCommand(this));
        commandManager.register(new ToggleNotificationCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, commandManager);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MainEventListener(this));
        long b = System.currentTimeMillis() - a;
        logHelper.info("&7Took &d" + b + "ms&7 to load");
    }

    @Override
    public void onDisable() {
        long a = System.currentTimeMillis();
        logHelper.info("&7Unloading...");
        userDataService.unload();
        antiBotManager.getBlackListService().unload();
        antiBotManager.getWhitelistService().unload();
        logHelper.info("&dThanks for choosing us!");
        long b = System.currentTimeMillis() - a;
        logHelper.info("&7Took &d" + b + "ms&7 to unload");
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
}
