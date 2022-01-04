package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import me.kr1s_d.ultimateantibot.common.objects.user.PlayerProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataService implements IService {
    private final IConfiguration database;
    private final LogHelper logHelper;
    private final Map<String, Boolean> ipMap;

    public UserDataService(IConfiguration database, IAntiBotPlugin plugin){
        this.logHelper = plugin.getLogHelper();
        this.ipMap = new HashMap<>();
        this.database = database;
    }

    @Override
    public void load() {
        for(String str : database.getStringList("data")){
            String[] part = str.split(";");
            ipMap.put(part[0], Boolean.valueOf(part[1]));
        }
        logHelper.info("&aLoaded " + ipMap.size() + " &ajoins!");
    }

    @Override
    public void unload() {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Boolean> map : ipMap.entrySet()){
            list.add(map.getKey() + ";" + map.getValue());
        }
        database.set("data", list);
        database.save();
    }

    public Map<String, Boolean> getFirstJoinMap() {
        return ipMap;
    }

    public void resetFirstJoin(String ip){
        ipMap.put(ip, true);
    }
}
