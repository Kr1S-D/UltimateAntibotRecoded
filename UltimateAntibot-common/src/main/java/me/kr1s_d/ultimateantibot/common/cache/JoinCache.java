package me.kr1s_d.ultimateantibot.common.cache;

import java.util.ArrayList;
import java.util.List;

public class JoinCache {
    private final List<String> lastIpJoined;

    public JoinCache(){
        this.lastIpJoined = new ArrayList<>();
    }

    public void addJoined(String ip){
        lastIpJoined.add(ip);
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
