package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICheck;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarNameCheck implements ICheck {

    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final Map<String, List<String>> data;
    private final List<String> suspects;

    public SimilarNameCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.data = new HashMap<>();
        this.suspects = new ArrayList<>();
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
        int i = 0;
        for(String str : joins) {
            for (int j = 0; j < name.length(); j++) {
                char actualChar = name.charAt(j);
                char checkChar = str.charAt(j);
                if(actualChar == checkChar){
                    i++;
                    if(i >= ConfigManger.getSimilarNameCheckConfig().getCondition()){
                        suspects.add(ip);
                    }
                }
            }
        }
        add(ip, name);
        if(suspects.size() >= ConfigManger.getSimilarNameCheckConfig().getTrigger()){
            if (ConfigManger.getSimilarNameCheckConfig().isKick()) {
                suspects.forEach(a -> {
                    plugin.disconnect(a, MessageManager.getSafeModeMessage());
                });
            }
            if(ConfigManger.getSimilarNameCheckConfig().isBlacklist()){
                suspects.forEach(a -> {
                    antiBotManager.getBlackListService().blacklist(a, MessageManager.reasonStrangePlayer);
                });
            }
            if(ConfigManger.getSimilarNameCheckConfig().isEnableAntiBotMode()){
                    antiBotManager.enableSlowAntiBotMode();
            }
            suspects.clear();

            return true;
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.getSimilarNameCheckConfig().isEnabled();
    }

    @Override
    public void loadTask() {
       plugin.scheduleRepeatingTask(() -> {
           suspects.clear();
           data.clear();
       }, false, 1000L * ConfigManger.getSimilarNameCheckConfig().getTime());
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
