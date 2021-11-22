package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.user.PlayerProfile;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class FirstJoinCheck{

    private final UserDataService userDataService;

    public FirstJoinCheck(IAntiBotPlugin plugin){
        this.userDataService = plugin.getUserDataService();
    }

    /**
     * @param ip - ip of the player
     * @param name - name of the player
     * @param uuid - uuid of player
     * @return - false if player can join or true if is first join
     */
    public boolean check(String ip, String name, String uuid) {
        if(!isEnabled()){
            return false;
        }
        PlayerProfile profile = userDataService.getFromIP(ip);
        if(profile == null){
            profile = new PlayerProfile(name, uuid, ip, 0, true);
        }
        if(profile.isFirstJoin()){
            profile.setFirstJoin(false);
            return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return ConfigManger.isFirstJoinEnabled;
    }
}
