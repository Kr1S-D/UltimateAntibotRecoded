package me.kr1s_d.ultimateantibot.common.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.IService;
import me.kr1s_d.ultimateantibot.common.UnderAttackMethod;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.profile.WhitelistEntry;

import java.util.*;

public class WhitelistService implements IService {
    private final QueueService queueService;
    private final Set<WhitelistEntry> timedWhitelist;
    private final Cache<String, Boolean> whitelist;
    private final IConfiguration whitelistConfig;
    private final LogHelper logHelper;

    public WhitelistService(QueueService queueService, IConfiguration whitelistConfig, LogHelper logHelper){
        this.queueService = queueService;
        this.whitelist = Caffeine.newBuilder()
                .build();
        this.timedWhitelist = new HashSet<>();
        this.whitelistConfig = whitelistConfig;
        this.logHelper = logHelper;
    }

    @Override
    public void load() {
        try {
            for (String ip : whitelistConfig.getStringList("data")) {
                whitelist.put(ip, true);
            }
            logHelper.info("&c" + whitelist.estimatedSize() + " &fIP added to whitelist!");
        }
        catch (Exception e){
            logHelper.error("Error while loading whitelist...");
            e.printStackTrace();
        }
    }

    public void checkWhitelisted() {
        timedWhitelist.removeIf(s -> {
            if(s.canBeRemoved()) {
                unWhitelist(s.getIp());
                return true;
            }
            return false;
        });
    }

    @Override
    public void unload() {
        try {
            List<String> serialized = new ArrayList<>();
            for (Map.Entry<String, Boolean> converted : whitelist.asMap().entrySet()) {
                serialized.add(converted.getKey());
            }
            whitelistConfig.set("data", serialized);
        }
        catch (Exception ignored){
            logHelper.error("Error while saving whitelist...");
        }

        whitelistConfig.save();
    }

    public void save() {
        try {
            List<String> serialized = new ArrayList<>();
            for (Map.Entry<String, Boolean> converted : whitelist.asMap().entrySet()) {
                serialized.add(converted.getKey());
            }
            whitelistConfig.set("data", serialized);
        }
        catch (Exception ignored){
            logHelper.error("Error while saving whitelist...");
        }

        whitelistConfig.save();
    }

    public int size() {
        return (int) whitelist.estimatedSize();
    }

    @UnderAttackMethod
    public void whitelist(String ip){
        whitelist.put(ip, true);
        queueService.removeQueue(ip);
    }

    @UnderAttackMethod
    public void whitelist(String ip, int minutes) {
        whitelist.put(ip, true);
        timedWhitelist.add(new WhitelistEntry(ip, minutes));
    }

    @UnderAttackMethod
    public void clear() {
        whitelist.invalidateAll();
    }

    @UnderAttackMethod
    public void unWhitelist(String ip){
        whitelist.invalidate(ip);
    }

    @UnderAttackMethod
    public boolean isWhitelisted(String ip){
        return whitelist.getIfPresent(ip) != null;
    }

    @UnderAttackMethod
    public void whitelistAll(String... ip){
        for (String i : ip) {
            whitelist(i);
        }
    }

    @UnderAttackMethod
    public void removeAll(String... ip){
        for (String s : ip) {
            unWhitelist(s);
        }
    }

    public Collection<String> getWhitelistedIPS() {
        return whitelist.asMap().entrySet().stream().collect(ArrayList::new, (accumulator, a) -> accumulator.add(a.getKey()), (a, b) -> {});
    }
}
