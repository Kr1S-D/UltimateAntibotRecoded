package me.kr1s_d.ultimateantibot.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class JoinCache {
    private final Map<String, Long> lastIpJoined;

    public JoinCache() {
        this.lastIpJoined = new HashMap<>();
    }

    public void addJoined(String ip) {
        lastIpJoined.put(ip, System.currentTimeMillis());
    }

    public void removeJoined(String ip){
        lastIpJoined.remove(ip);
    }

    public void clear() {
        lastIpJoined.clear();
    }

    public List<String> getJoined(int minSec) {
        List<String> ip = new ArrayList<>();

        for(Map.Entry<String, Long> map : lastIpJoined.entrySet()){
            int second = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - map.getValue());

            if(second <= minSec){
                ip.add(map.getKey());
            }
        }

        return ip;
    }
}
