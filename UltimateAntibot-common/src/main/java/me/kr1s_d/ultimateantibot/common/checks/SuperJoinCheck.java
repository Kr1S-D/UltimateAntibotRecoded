package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICheck;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.Map;

public class SuperJoinCheck implements ICheck {

    private final IAntiBotPlugin plugin;
    private final Map<String, Integer> data;

    public SuperJoinCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.data = new HashMap<>();
        loadTask();
        if(isEnabled()) plugin.getLogHelper().debug("SuperJoinCheck initialized!");;
    }

    @Override
    public boolean needToDeny(String ip, String name) {
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
    public boolean isEnabled() {
        return ConfigManger.isSuperJoinEnabled;
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.superJoinTime);
    }
}
