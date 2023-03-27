package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.IService;
import me.kr1s_d.ultimateantibot.common.objects.profile.WhitelistEntry;

import java.util.*;

public class WhitelistService implements IService {
    private final QueueService queueService;
    private final List<WhitelistEntry> timedWhitelist;
    private final Set<String> whitelist;
    private final IConfiguration whitelistConfig;
    private final LogHelper logHelper;

    public WhitelistService(QueueService queueService, IConfiguration whitelistConfig, LogHelper logHelper){
        this.queueService = queueService;
        this.whitelist = new HashSet<>();
        this.timedWhitelist = new ArrayList<>();
        this.whitelistConfig = whitelistConfig;
        this.logHelper = logHelper;
    }

    @Override
    public void load() {
        try {
            whitelist.addAll(whitelistConfig.getStringList("data"));
            logHelper.info("&c" + whitelist.size() + " &fIP added to whitelist!");
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
            whitelistConfig.set("data", new ArrayList<>(whitelist));
        }
        catch (Exception ignored){
            logHelper.error("Error while saving whitelist...");
        }

        whitelistConfig.save();
    }

    public void save(){
        try {
            whitelistConfig.set("data", new ArrayList<>(whitelist));
        }
        catch (Exception ignored){
            logHelper.error("Error while saving whitelist...");
        }

        whitelistConfig.save();
    }

    public int size() {
        return whitelist.size();
    }

    public void whitelist(String ip){
        whitelist.add(ip);
        queueService.removeQueue(ip);
    }

    public void whitelist(String ip, int minutes) {
        whitelist.add(ip);
        timedWhitelist.add(new WhitelistEntry(ip, minutes));
    }

    public void clear() {
        whitelist.clear();
    }

    public void unWhitelist(String ip){
        whitelist.remove(ip);
    }

    public boolean isWhitelisted(String ip){
        return whitelist.contains(ip);
    }

    public void whitelistAll(String... ip){
        whitelist.addAll(Arrays.asList(ip));
    }

    public void removeAll(String... ip){
        Arrays.asList(ip).forEach(whitelist::remove);
    }

    public Collection<String> getWhitelistedIPS() {
        return whitelist;
    }
}
