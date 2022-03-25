package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IBasicCheck;
import me.kr1s_d.ultimateantibot.common.objects.base.SlowJoinCheckConfiguration;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LengthBasicCheck implements IBasicCheck {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final List<String> lastNicks;
    private final Set<String> suspects;
    private final SlowJoinCheckConfiguration config;

    public LengthBasicCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.lastNicks = new ArrayList<>();
        this.suspects = new HashSet<>();
        this.config = ConfigManger.getLenghtCheckConfig();
        loadTask();
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()) return false;
        if(lastNicks == null){
            add(ip, name);
            return false;
        }
        for(String str : lastNicks) {
           if(str.length() == name.length()) {
               if (suspects.add(ip)) {
                   plugin.getLogHelper().debug("LenghtCheck > Possible bot found: " + ip + " " + name);
               }
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
                antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER, name);
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
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(() -> {
            suspects.clear();
            lastNicks.clear();
        }, false, 1000L * config.getTime());
    }

    private void add(String ip, String name){
        if(lastNicks.contains(name)) return;
        if(lastNicks.size() >= 3){
            lastNicks.remove(0);
            lastNicks.add(name);
        }else{
            lastNicks.add(name);
        }
    }
}
