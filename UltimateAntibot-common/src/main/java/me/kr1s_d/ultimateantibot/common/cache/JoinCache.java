package me.kr1s_d.ultimateantibot.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JoinCache {
    private final Cache<String, Long> lastIpJoined;

    public JoinCache() {
        this.lastIpJoined = Caffeine.newBuilder()
                .expireAfterWrite(ConfigManger.joinCacheJoinMinutes, TimeUnit.MINUTES)
                .build();
    }

    public void addJoined(String ip) {
        lastIpJoined.put(ip, System.currentTimeMillis());
    }

    public void removeJoined(String ip){
        lastIpJoined.invalidate(ip);
    }

    public void clear() {
        lastIpJoined.invalidateAll();
    }

    public List<String> getJoined() {
        List<String> ip = new ArrayList<>();

        for(Map.Entry<String, Long> map : lastIpJoined.asMap().entrySet()) {
            int second = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - map.getValue());

            if(second <= ConfigManger.joinCacheJoinMinutes){
                ip.add(map.getKey());
            }
        }

        return ip;
    }

    public boolean isJoined(String ip) {
        List<String> joined = getJoined();
        return joined.contains(ip);
    }
}
