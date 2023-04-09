package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.service.CheckService;

import java.util.ArrayList;
import java.util.List;

public class SlowJoinCheck implements JoinCheck {
    private final IAntiBotPlugin plugin;
    private final List<String> lastNames;

    public SlowJoinCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.lastNames = new ArrayList<>();

        if(isEnabled()){
            loadTask();
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        return false;
    }

    @Override
    public CheckType getType() {
        return CheckType.SLOW_JOIN;
    }

    @Override
    public void onDisconnect(String ip, String name) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public long getCacheSize() {
        return -1;
    }

    @Override
    public void clearCache() {
        //not supported here
    }

    @Override
    public void removeCache(String ip) {

    }

    public void loadTask() {
        plugin.scheduleRepeatingTask(lastNames::clear, false, 1000L * 60L);
    }
}
