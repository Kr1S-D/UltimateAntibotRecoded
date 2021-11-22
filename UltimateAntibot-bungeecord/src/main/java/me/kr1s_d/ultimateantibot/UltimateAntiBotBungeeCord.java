package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.filter.LogFilter;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.Version;
import me.kr1s_d.ultimateantibot.core.UltimateAntiBotCore;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

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
        this.logHelper = new LogHelper(ProxyServer.getInstance().getLogger());
        logHelper.info("&aLoading UltimateAntiBot...");
        this.antiBotManager = new AntBotManager(this);
        core = new UltimateAntiBotCore(this);
        core.load();
        ConfigManger.init(config);
        MessageManager.init(messages);
        Version.init(this);
        antiBotManager.getQueueService().load();
        antiBotManager.getWhitelistService().load();
        antiBotManager.getBlackListService().load();
        userDataService = new UserDataService(database, this);
        userDataService.load();
        ProxyServer.getInstance().getLogger().setFilter(new LogFilter(antiBotManager));
        logHelper.info("&aLoaded UltimateAntiBot!");
        logHelper.sendLogo();
        logHelper.info("&aVersion: $1 | Author: $2 | Cores: $3"
                .replace("$1", this.getDescription().getVersion())
                .replace("$2", this.getDescription().getAuthor())
                .replace("$3", String.valueOf(Version.getCores()))
        );
        long b = System.currentTimeMillis() - a;
        logHelper.info("&eTook " + b + "ms to load");
    }

    @Override
    public void onDisable() {
        long a = System.currentTimeMillis();
        logHelper.info("&aUnloading...");
        runTask(() -> {
            userDataService.unload();
            antiBotManager.getBlackListService().unload();
            antiBotManager.getWhitelistService().unload();
            logHelper.info("&atThanks for choosing us!");
            long b = System.currentTimeMillis() - a;
            logHelper.info("&eTook " + b + "ms to unload");
        }, true);
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
            scheduler.schedule(this, ()-> scheduler.runAsync(this, runnable), repeatMilliseconds, TimeUnit.MILLISECONDS);
        }else {
            scheduler.schedule(this, runnable, repeatMilliseconds, TimeUnit.MILLISECONDS);
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
}
