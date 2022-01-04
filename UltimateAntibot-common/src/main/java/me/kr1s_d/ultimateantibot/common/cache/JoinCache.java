package me.kr1s_d.ultimateantibot.common.cache;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;

import java.util.ArrayList;
import java.util.List;

public class JoinCache {
    private final List<String> lastIpJoined;
    private final IAntiBotPlugin iAntiBotPlugin;

    public JoinCache(IAntiBotPlugin iAntiBotPlugin){
        this.iAntiBotPlugin = iAntiBotPlugin;
        this.lastIpJoined = new ArrayList<>();
    }

    public void addJoined(String ip){
        lastIpJoined.add(ip);
        iAntiBotPlugin.scheduleDelayedTask(() -> removeJoined(ip), false,1000L * 30L);
    }

    public void removeJoined(String ip){
        lastIpJoined.remove(ip);
    }

    public void clear(){
        lastIpJoined.clear();
    }

    public List<String> getJoined() {
        return lastIpJoined;
    }
}
