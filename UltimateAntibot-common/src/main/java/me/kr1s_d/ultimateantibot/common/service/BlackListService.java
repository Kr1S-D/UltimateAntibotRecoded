package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;

import java.util.HashSet;
import java.util.Set;

public class BlackListService implements IService {

    private final Set<String> blacklist;
    private final IConfiguration blacklistConfig;

    public BlackListService(IConfiguration blacklistConfig){
        this.blacklist = new HashSet<>();
        this.blacklistConfig = blacklistConfig;
        load();
    }

    @Override
    public void load() {
        blacklist.addAll(blacklistConfig.getConfigurationSection("data"));
    }

    @Override
    public void unload() {
        blacklistConfig.set("data", blacklist);
    }

    public int size(){
        return blacklist.size();
    }

    public void blacklist(String ip){
        blacklist.add(ip);
    }

    public void clear(){
        blacklist.clear();
    }

    public void unBlacklist(String ip){
        blacklist.remove(ip);
    }

    public boolean isBlackListed(String ip){
        return blacklist.contains(ip);
    }
}
