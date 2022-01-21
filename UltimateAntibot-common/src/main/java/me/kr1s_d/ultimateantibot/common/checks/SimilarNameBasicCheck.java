package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IBasicCheck;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.*;

public class SimilarNameBasicCheck implements IBasicCheck {

    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final List<String> joins;
    private final List<String> suspects;
    private int checks;

    public SimilarNameBasicCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.joins = new ArrayList<>();
        this.suspects = new ArrayList<>();
        this.checks = 0;
        loadTask();
    }

    @Override
    public boolean needToDeny(String ip, String name) {
        if(!isEnabled()) return false;
        if(joins.size() == 0){
            add(ip, name);
            return false;
        }
        int similar = countChars(name);
        if(similar >= ConfigManger.getSimilarNameCheckConfig().getCondition()){
            suspects.add(ip);
        }else{
            checks++;
        }
        if(checks > 7){
            joins.clear();
        }
        add(ip, name);
        if(suspects.size() >= ConfigManger.getSimilarNameCheckConfig().getTrigger()){
            if (ConfigManger.getSimilarNameCheckConfig().isKick()) {
                suspects.forEach(a -> {
                    plugin.disconnect(a, MessageManager.getSafeModeMessage());
                });
            }
            if(ConfigManger.getSimilarNameCheckConfig().isBlacklist()){
                antiBotManager.getBlackListService().blacklist(ip, MessageManager.reasonStrangePlayer, name);
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
           joins.clear();
       }, false, 1000L * ConfigManger.getSimilarNameCheckConfig().getTime());
    }

    private void add(String ip, String name){
        List<String> lastNicks = joins;
        if(lastNicks.size() >= 3){
            lastNicks.remove(0);
            lastNicks.add(name);
        }else{
            lastNicks.add(name);
        }
    }

    private int countChars(String toCount){
        Map<String, Integer> map = new HashMap<>();
        int a = 0;
        for(String s : joins){
            map.put(s, a);
            a++;
        }
        int[] theI = new int[3];
        for(Map.Entry<String, Integer> map1 : map.entrySet()){
            String str = map1.getKey();
            int checkLength = str.length();
            int length = toCount.length();
            int safe = Math.min(checkLength, length);
            for(int j = 0; j < safe; j++){
                char c1 = toCount.charAt(j);
                char c2 = str.charAt(j);
                if(c1 == c2){
                    theI[map1.getValue()]++;
                }
            }
        }
        plugin.getLogHelper().debug("VALUES : " + Arrays.toString(theI));
        plugin.getLogHelper().debug("MAX VALUE: " + max(theI[0], theI[1], theI[2]));
        return max(theI[0], theI[1], theI[2]);
    }

    public static int max(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
}
