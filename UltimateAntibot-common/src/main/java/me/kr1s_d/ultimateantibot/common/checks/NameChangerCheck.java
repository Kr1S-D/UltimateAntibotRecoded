package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICheck;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameChangerCheck implements ICheck {

    private final ICore iCore;
    private final Map<String, List<String>> data;

    public NameChangerCheck(IAntiBotPlugin iAntiBotPlugin){
        this.iCore = iAntiBotPlugin.getCore();
        this.data = new HashMap<>();
        loadTask();
    }

    /**
     * @param ip ip of the player
     * @param name name of the player
     * @param uuid uuid of player
     * @return if player neeed to be disconnected from the check
     */
    public boolean needToDeny(String ip, String name, String uuid){
        if(!isEnabled()){
            return false;
        }
        if(data.containsKey(ip)){
            List<String> nicks = data.get(ip);
            nicks.add(name);
            return nicks.size() > ConfigManger.nameChangerLimit;
        }else{
            data.put(ip, new ArrayList<>());
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isNameChangerEnabled;
    }

    @Override
    public void loadTask() {
        iCore.addNewThread(data::clear, 1000L * ConfigManger.nameChangerTime);
    }


}
