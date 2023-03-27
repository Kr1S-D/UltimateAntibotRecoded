package me.kr1s_d.ultimateantibot.common.checks.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NameChangerCheck implements JoinCheck {
    private final IAntiBotPlugin plugin;
    private final Cache<String, Set<String>> data;

    public NameChangerCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.data = Caffeine.newBuilder()
                .expireAfterWrite(ConfigManger.nameChangerTime, TimeUnit.SECONDS)
                .build();

        if(isEnabled()){
            //loadTask();
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    /**
     * @param ip   ip of the player
     * @param name name of the player
     * @return if player need to be disconnected from the check
     */
    public boolean isDenied(String ip, String name) {
        if (!isEnabled()) {
            return false;
        }
        if(!plugin.getAntiBotManager().isSomeModeOnline()) return false;

        Set<String> nicks = data.get(ip, k -> new HashSet<>());
        nicks.add(name);

        return nicks.size() >= ConfigManger.nameChangerLimit;
    }

    @Override
    public CheckType getType() {
        return CheckType.NAME_CHANGER;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isNameChangerEnabled;
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

    //public void loadTask() {
    //    plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.nameChangerTime);
    //}
}
