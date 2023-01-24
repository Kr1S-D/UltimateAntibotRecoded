package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.UABRunnable;

public class FilesUpdater {
    private final IAntiBotPlugin plugin;

    private final IConfiguration config;
    private final IConfiguration messages;
    private final IConfiguration whitelist;
    private final IConfiguration blacklist;

    private boolean isDeleted;
    private boolean upToDate;
    private int i;

    public FilesUpdater(IAntiBotPlugin plugin, IConfiguration config, IConfiguration messages, IConfiguration whitelist, IConfiguration blacklist) {
        this.plugin = plugin;

        this.config = config;
        this.messages = messages;
        this.whitelist = whitelist;
        this.blacklist = blacklist;

        this.isDeleted = false;
        this.i = 0;
    }

    public void check(double fc, double fm) {
        double cc = getVersion(config);
        double cm = getVersion(messages);

        if(cc == fc && fm == cm) {
            return;
        }

        if(cc < 4) {
            isDeleted = true;
            config.destroy();
            messages.destroy();
            whitelist.destroy();
            blacklist.destroy();
            return;
        }

        if(cc != fc || cm != fm){
            upToDate = true;
            plugin.scheduleRepeatingTask(new UABRunnable() {
                @Override
                public boolean isAsync() {
                    return false;
                }

                @Override
                public long getPeriod() {
                    return 1000;
                }

                @Override
                public void run() {
                    i++;
                    if(i >= 10) cancel();
                    if(plugin.getLogHelper() == null) {
                        return;
                    }
                    plugin.getLogHelper().warn("Unable to read config.yml and messages.yml! Please regenerate them as soon as possible and restart the server!");
                }
            });
        }
    }

    public boolean requiresReassign() {
        return isDeleted;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    private double getVersion(IConfiguration config) {
        return config.getDouble("version");
    }
}
