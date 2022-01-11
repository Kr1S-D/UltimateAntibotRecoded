package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICheck;
import me.kr1s_d.ultimateantibot.common.objects.other.SlowJoinCheckConfiguration;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LengthCheck implements ICheck {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final Map<String, List<String>> data;
    private final List<String> suspects;
    private final SlowJoinCheckConfiguration config;

    public LengthCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.data = new HashMap<>();
        this.suspects = new ArrayList<>();
        this.config = ConfigManger.getLenghtCheckConfig();
        loadTask();
    }

    @Override
    public boolean needToDeny(String ip, String name) {
        if(!isEnabled()) return false;
        List<String> joins = data.get(ip);
        if(joins == null){
            add(ip, name);
            return false;
        }
        for(String str : joins) {
           if(str.length() == name.length()){
               suspects.add(ip);
           }
        }
        add(ip, name);
        if(suspects.size() >= config.getTrigger()){
            if (config.isKick()) {
                suspects.forEach(a -> {
                    plugin.disconnect(a, MessageManager.getSafeModeMessage());
                });
            }
            if(config.isBlacklist()){
                suspects.forEach(a -> {
                    antiBotManager.getBlackListService().blacklist(a, MessageManager.reasonStrangePlayer);
                });
            }
            if(config.isEnableAntiBotMode()){
                    antiBotManager.enableSlowAntiBotMode();
            }
            suspects.clear();

            return true;
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(() -> {
            suspects.clear();
            data.clear();
        }, false, 1000L * config.getTime());
    }

    private void add(String ip, String name){
        List<String> lastNicks = data.getOrDefault(ip, new ArrayList<>());
        if(lastNicks.size() >= 3){
            lastNicks.remove(0);
            lastNicks.add(name);
        }else{
            lastNicks.add(name);
        }
        data.put(ip, lastNicks);
    }
}
