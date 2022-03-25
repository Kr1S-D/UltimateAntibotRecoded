package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IBasicCheck;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.Map;

public class SuperJoinBasicCheck implements IBasicCheck {

    private final IAntiBotPlugin plugin;
    private final Map<String, Integer> data;

    public SuperJoinBasicCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.data = new HashMap<>();
        loadTask();
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()){
            return false;
        }
        if(data.containsKey(ip)){
            int times = data.get(ip);
            if(times > ConfigManger.superJoinLimit){
                return true;
            }
            data.put(ip, times + 1);
        }else{
            data.put(ip, 0);
        }
        return false;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isSuperJoinEnabled;
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.superJoinTime);
    }
}
