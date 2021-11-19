package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;

import java.util.HashSet;
import java.util.Set;

public class QueueService implements IService {

    private Set<String> queue;

    @Override
    public void load() {
        this.queue = new HashSet<>();
    }

    @Override
    public void unload() {
        queue.clear();
    }

    public boolean isQueued(String ip){
        return queue.contains(ip);
    }

    public void queue(String ip){
        queue.add(ip);
    }

    public void removeQueue(String ip){
        queue.remove(ip);
    }

    public int size(){
        return queue.size();
    }
}
