package me.kr1s_d.ultimateantibot.common.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.kr1s_d.ultimateantibot.common.IService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class QueueService implements IService {
    private final Cache<String, Boolean> queue;
    private long lastCleanUP;

    public QueueService(){
        this.queue = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ConfigManger.taskManagerClearCache))
                .build();
        this.lastCleanUP = 0;
    }

    @Override
    public void load() {}

    @Override
    public void unload() {
        queue.invalidateAll();
    }

    public boolean isQueued(String ip){
        return queue.getIfPresent(ip) != null;
    }

    public void queue(String ip){
        queue.put(ip, true);
    }

    public void removeQueue(String ip){
        queue.invalidate(ip);
    }

    public int size() {
        if(System.currentTimeMillis() - lastCleanUP >= 20000) {
            queue.cleanUp();
            lastCleanUP = System.currentTimeMillis();
        }
        return (int) queue.estimatedSize();
    }

    public void clear() {
        queue.invalidateAll();
    }
}
