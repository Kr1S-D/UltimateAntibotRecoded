package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataService implements IService {
    private final IAntiBotPlugin plugin;
    private final IConfiguration database;
    private final LogHelper logHelper;
    private final Map<String, Boolean> ipMap;

    public UserDataService(IConfiguration database, IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.ipMap = new HashMap<>();
        this.database = database;
    }

    @Override
    public void load() {
        for(String str : database.getStringList("data")){
            String[] part = str.split(";");
            if(plugin.getAntiBotManager().getWhitelistService().isWhitelisted(part[0])) {
                ipMap.put(part[0], Boolean.valueOf(part[1]));
            }
        }
        logHelper.info("&fLoaded &c" + ipMap.size() + " &fjoins!");
    }

    @Override
    public void unload() {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Boolean> map : ipMap.entrySet()){
            if(plugin.getAntiBotManager().getWhitelistService().isWhitelisted(map.getKey())) {
                list.add(map.getKey() + ";" + map.getValue());
            }
        }
        database.set("data", list);
        database.save();
    }

    public void save(){
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Boolean> map : ipMap.entrySet()){
            if(plugin.getAntiBotManager().getWhitelistService().isWhitelisted(map.getKey())) {
                list.add(map.getKey() + ";" + map.getValue());
            }
        }
        database.set("data", list);
        database.save();
        ipMap.clear();
        for(String str : database.getStringList("data")){
            String[] part = str.split(";");
            if(plugin.getAntiBotManager().getWhitelistService().isWhitelisted(part[0])) {
                ipMap.put(part[0], Boolean.valueOf(part[1]));
            }
        }
    }

    public Map<String, Boolean> getFirstJoinMap() {
        return ipMap;
    }

    public void resetFirstJoin(String ip){
        ipMap.put(ip, false);
    }

    public int size(){
        return ipMap.size();
    }
}
