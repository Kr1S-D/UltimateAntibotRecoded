package me.kr1s_d.ultimateantibot.common.checks.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import java.util.concurrent.TimeUnit;

public class SuperJoinCheck implements JoinCheck {
    private final IAntiBotPlugin plugin;
    private final Cache<String, FancyInteger> data;

    public SuperJoinCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.data = Caffeine.newBuilder()
                .expireAfterWrite(ConfigManger.superJoinTime, TimeUnit.SECONDS)
                .build();

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
        FancyInteger i = data.get(ip, k -> new FancyInteger(0));
        i.increase();

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
        return data.estimatedSize();
    }

    @Override
    public void clearCache() {
        data.invalidateAll();
    }

    @Override
    public void removeCache(String ip) {
        data.invalidate(ip);
    }

}
