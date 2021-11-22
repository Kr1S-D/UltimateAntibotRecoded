package me.kr1s_d.ultimateantibot.common.cache;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;

import java.util.ArrayList;
import java.util.List;

public class JoinCache {
    private final List<String> lastIpJoined;
    private final ICore uabCore;

    public JoinCache(ICore iCore){
        this.uabCore = iCore;
        this.lastIpJoined = new ArrayList<>();
    }

    public void addJoined(String ip){
        lastIpJoined.add(ip);
        uabCore.addNewThread(() -> removeJoined(ip), 1000L * 30L);
    }

    public void removeJoined(String ip){
        lastIpJoined.remove(ip);
    }

    public void clear(){
        lastIpJoined.clear();
    }

}
