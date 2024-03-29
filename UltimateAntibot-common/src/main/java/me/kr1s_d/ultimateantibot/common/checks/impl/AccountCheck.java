package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.objects.config.SlowCheckConfig;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccountCheck implements JoinCheck {

    private final IAntiBotPlugin plugin;
    private final Map<String, Set<String>> map;
    private final SlowCheckConfig config;

    public AccountCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.map = new HashMap<>();
        this.config = ConfigManger.getAccountCheckConfig();

        if(isEnabled()){
            loadTask();
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if (!isEnabled()) return false;
        Set<String> a = map.getOrDefault(ip, new HashSet<>());
        a.add(name);
        map.put(ip, a);

        if (map.get(ip).size() >= ConfigManger.getAccountCheckConfig().getTrigger()) {
            Set<String> subs = map.get(ip);

            if (config.isKick()) {
                subs.forEach(b -> {
                    plugin.disconnect(b, MessageManager.getAccountOnlineMessage());
                });
            }

            if (config.isBlacklist()) {
                plugin.getAntiBotManager().getBlackListService().blacklist(ip, BlackListReason.TOO_MUCH_NAMES);
            }

            if (config.isEnableAntiBotMode()) {
                plugin.getAntiBotManager().enableSlowAntiBotMode();
            }

            subs.clear();
            return true;
        }

        return false;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public long getCacheSize() {
        return map.size();
    }

    @Override
    public void clearCache() {
        map.clear();
    }

    @Override
    public void removeCache(String ip) {
        map.remove(ip);
    }

    public void loadTask() {
        plugin.scheduleRepeatingTask(map::clear, false, 1000L * 300L); //300 sec
    }

    @Override
    public CheckType getType() {
        return CheckType.ACCOUNT;
    }

    public void onDisconnect(String ip, String name){
        map.remove(ip);
    }
}
