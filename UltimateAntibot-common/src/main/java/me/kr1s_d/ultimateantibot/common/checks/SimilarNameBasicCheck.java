package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.enums.CheckListenedEvent;
import me.kr1s_d.ultimateantibot.common.objects.enums.CheckPriority;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IBasicCheck;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IManagedCheck;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.*;

public class SimilarNameBasicCheck extends IManagedCheck {

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
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()) return false;
        if(joins.size() == 0){
            add(ip, name);
            return false;
        }
        //if already contains remove dont check
        if(joins.contains(name)){
            return false;
        }
        int similar = countChars(name);
        if(similar >= ConfigManger.getSimilarNameCheckConfig().getCondition()){
            suspects.add(ip);
            plugin.getLogHelper().debug("SimilarNameCheck > Possible bot found: " + ip + " " + name);
        }else{
            checks++;
        }
        //why this?
        if(checks > 10){
            joins.clear();
        }
        //Register default ip
        add(ip, name);
        //blacklist actions
        if(suspects.size() >= ConfigManger.getSimilarNameCheckConfig().getTrigger()){
            if (ConfigManger.getSimilarNameCheckConfig().isKick()) {
                new ArrayList<>(suspects).forEach(a -> {
                    plugin.disconnect(a, MessageManager.getSafeModeMessage());
                });
            }
            if(ConfigManger.getSimilarNameCheckConfig().isBlacklist()){
                antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER, name);
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
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public String getCheckName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public double getCheckVersion() {
        return 4.0;
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

    @Override
    public CheckPriority getCheckPriority() {
        return CheckPriority.HIGH;
    }

    @Override
    public CheckListenedEvent getCheckListenedEvent() {
        return CheckListenedEvent.POSTLOGIN;
    }

    @Override
    public void onCancel(String ip, String name) {
        plugin.disconnect(ip, MessageManager.getSafeModeMessage());
        plugin.getLogHelper().debug("Similar Name Check!");
    }

    @Override
    public boolean requireAntiBotMode() {
        return false;
    }

    private void add(String ip, String name){
        //add the player in joinlist removing the last
        List<String> lastNicks = joins;
        if(lastNicks.size() >= 3){
            lastNicks.remove(lastNicks.get(0));
            lastNicks.add(name);
        }else{
            lastNicks.add(name);
        }
    }

    private int countChars(String toCount){
        //a charmap counter
        Map<String, Integer> map = new HashMap<>();
        //value to increase
        int a = 0;
        for(String s : joins){
            map.put(s, a);
            a++;
        }
        int[] theI = new int[3];
        //count chars
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
        plugin.getLogHelper().debug("SimilarNameCheck > VALUES : " + Arrays.toString(theI));
        plugin.getLogHelper().debug("SimilarNameCheck > MAX VALUE: " + max(theI[0], theI[1], theI[2]));
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
