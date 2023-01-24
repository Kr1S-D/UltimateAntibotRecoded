package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.List;

public class InvalidNameCheck implements JoinCheck {
    private final IAntiBotManager antiBotManager;
    private final List<String> invalidNames;
    
    public InvalidNameCheck(IAntiBotPlugin plugin) {
        this.antiBotManager = plugin.getAntiBotManager();
        this.invalidNames = ConfigManger.invalidNamesBlockedEntries;
    }

    @Override
    public boolean isDenied(String ip, String name) {
        for(String blacklisted : invalidNames){
            blacklisted = blacklisted.toLowerCase();

            // TODO: 03/09/2022 da registrare 
            
            if(name.toLowerCase().contains(blacklisted)) {
                antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER, name);
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isInvalidNameCheckEnabled;
    }

    @Override
    public void loadTask() {

    }
}
