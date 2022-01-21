package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IBasicCheck;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NameChangerBasicCheck implements IBasicCheck {

    private final IAntiBotPlugin plugin;
    private final Map<String, Set<String>> data;

    public NameChangerBasicCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.data = new HashMap<>();
        loadTask();
        if(isEnabled()) plugin.getLogHelper().debug("NameChangerCheck initialized!");;
    }

    /**
     * @param ip ip of the player
     * @param name name of the player
     * @return if player neeed to be disconnected from the check
     */
    public boolean needToDeny(String ip, String name){
        if(!isEnabled()){
            return false;
        }
        if(data.containsKey(ip)){
            Set<String> nicks = data.get(ip);
            nicks.add(name);
            return nicks.size() >= ConfigManger.nameChangerLimit;
        }else{
            data.put(ip, new HashSet<>());
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isNameChangerEnabled;
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.nameChangerTime);
    }


}
