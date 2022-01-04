package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import sun.rmi.runtime.Log;

import java.util.HashSet;
import java.util.Set;

public class WhitelistService implements IService {

    private final Set<String> whitelist;
    private final IConfiguration whitelistList;
    private final LogHelper logHelper;

    public WhitelistService(IConfiguration whitelistList, LogHelper logHelper){
        this.whitelist = new HashSet<>();
        this.whitelistList = whitelistList;
        this.logHelper = logHelper;
    }

    @Override
    public void load() {
        whitelist.addAll(whitelistList.getConfigurationSection("data"));
        logHelper.info("&f" + whitelist.size() + " &dIP added to whitelist!");
    }

    @Override
    public void unload() {
        whitelistList.set("data", whitelist);
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
}
