package me.kr1s_d.ultimateantibot;

import me.kr1s_d.commons.helper.LogHelper;
import me.kr1s_d.ultimateantibot.objects.Config;
import me.kr1s_d.commons.objects.interfaces.IAntiBotPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import me.kr1s_d.commons.objects.interfaces.IAntiBotManager;
import me.kr1s_d.commons.objects.interfaces.IConfiguration;

import java.util.concurrent.TimeUnit;

public final class UltimateAntiBotBungeeCord extends Plugin implements IAntiBotPlugin {

    private TaskScheduler scheduler;
    private IConfiguration configuration;
    private IConfiguration messages;
    private IConfiguration whitelist;
    private IConfiguration blacklist;
    private IAntiBotManager antiBotManager;
    private LogHelper logHelper;

    @Override
    public void onEnable() {
        this.scheduler = ProxyServer.getInstance().getScheduler();
        this.configuration = new Config(this, "%datafolder%/config.yml");
        this.messages = new Config(this, "%datafolder%/messages.yml");
        this.whitelist = new Config(this, "%datafolder/whitelist.yml%");
        this.blacklist = new Config(this, "%datafolder%/blacklist.yml");
        this.antiBotManager = new AntBotManager(this);
        this.logHelper = new LogHelper(ProxyServer.getInstance().getLogger());
        logHelper.debug("&aCiao &badd &c&lcoasd");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void scheduleSyncDelayedTask(Runnable runnable, boolean async, long milliseconds){
        if(async){
            scheduler.schedule(this, ()-> scheduler.runAsync(this, runnable), milliseconds, TimeUnit.SECONDS);
        }else {
            scheduler.schedule(this, runnable, milliseconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public IConfiguration getConfig() {
        return null;
    }

    @Override
    public IConfiguration getMessages() {
        return null;
    }

    @Override
    public IConfiguration getWhitelist() {
        return null;
    }

    @Override
    public IConfiguration getBlackList() {
        return null;
    }

    @Override
    public IConfiguration getDatabase() {
        return null;
    }

    @Override
    public IAntiBotManager getAntiBotManager() {
        return null;
    }

    @Override
    public LogHelper getLogHelper() {
        return null;
    }
}
