package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICheck;
import me.kr1s_d.ultimateantibot.common.objects.other.SlowJoinCheckConfiguration;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccountCheck implements ICheck {

    private final IAntiBotPlugin plugin;
    private final Map<String, Set<String>> map;
    private final SlowJoinCheckConfiguration config;

    public AccountCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.map = new HashMap<>();
        this.config = ConfigManger.getAccountCheckConfig();
        loadTask();
    }

    @Override
    public boolean needToDeny(String ip, String name) {
        if(!isEnabled()) return false;
        Set<String> a = map.getOrDefault(ip, new HashSet<>());
        a.add(name);
        map.put(ip, a);
        if(map.get(ip).size() >= ConfigManger.getAccountCheckConfig().getTrigger()){
            Set<String> subs = map.get(ip);
            if (config.isKick()) {
                subs.forEach(b -> {
                    plugin.disconnect(b, MessageManager.getAccountOnlineMessage());
                });
            }
            if(config.isBlacklist()){
                subs.forEach(b -> {
                    plugin.getAntiBotManager().getBlackListService().blacklist(b, MessageManager.reasonTooManyNicks);
                });
            }
            if(config.isEnableAntiBotMode()){
                plugin.getAntiBotManager().enableSlowAntiBotMode();
            }
            subs.clear();
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

    }

    public void onDisconnect(String ip, String name){
        Set<String> a = map.getOrDefault(ip, new HashSet<>());
        a.remove(name);
        map.put(ip, a);
    }
}
