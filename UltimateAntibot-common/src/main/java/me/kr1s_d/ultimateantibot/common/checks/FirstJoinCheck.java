package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICheck;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.Map;

public class FirstJoinCheck implements ICheck {

    private final Map<String, Boolean> data;

    public FirstJoinCheck(IAntiBotPlugin plugin){
        UserDataService userDataService = plugin.getUserDataService();
        this.data = userDataService.getFirstJoinMap();
        if(isEnabled()){
            plugin.getLogHelper().debug("FirstJoinCheck initialized!");
        }
    }

    /**
     * @param ip - ip of the player
     * @param name - name of the player
     * @return - false if player can join or true if is first join
     */
    public boolean needToDeny(String ip, String name) {
        if(!isEnabled()){
            return false;
        }
        return isFirstJoin(ip);
    }

    public boolean isEnabled() {
        return ConfigManger.isFirstJoinEnabled;
    }

    @Override
    public void loadTask() {

    }

    public boolean isFirstJoin(String ip){
        if(data.containsKey(ip)){
            return data.get(ip);
        }
        data.put(ip, false);
        return true;
    }
}
