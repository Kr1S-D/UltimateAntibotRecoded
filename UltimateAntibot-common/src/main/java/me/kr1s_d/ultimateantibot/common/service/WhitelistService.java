package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;

import java.util.*;

public class WhitelistService implements IService {

    private final Set<String> whitelist;
    private final IConfiguration whitelistConfig;
    private final LogHelper logHelper;

    public WhitelistService(IConfiguration whitelistConfig, LogHelper logHelper){
        this.whitelist = new HashSet<>();
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

    public int size(){
        return whitelist.size();
    }

    public void whitelist(String ip){
        whitelist.add(ip);
    }

    public void clear(){
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
