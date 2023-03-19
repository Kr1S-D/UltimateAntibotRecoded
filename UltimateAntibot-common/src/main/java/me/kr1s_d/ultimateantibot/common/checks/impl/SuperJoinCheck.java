package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.objects.FancyInteger;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SuperJoinCheck implements JoinCheck {

    private final IAntiBotPlugin plugin;
    private final Map<String, FancyInteger> data;

    public SuperJoinCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.data = new ConcurrentHashMap<>();
        loadTask();
        if(isEnabled()){
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if (!isEnabled()) {
            return false;
        }
        FancyInteger i = data.getOrDefault(ip, new FancyInteger(0));
        i.increase();
        data.put(ip, i);

        return i.get() > ConfigManger.superJoinLimit;
    }

    @Override
    public CheckType getType() {
        return CheckType.SUPER_JOIN;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isSuperJoinEnabled;
    }

    @Override
    public long getCacheSize() {
        return data.size();
    }

    @Override
    public void clearCache() {
        data.clear();
    }

    @Override
    public void removeCache(String ip) {
        data.remove(ip);
    }

    public void loadTask() {
        plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.superJoinTime);
    }
}
