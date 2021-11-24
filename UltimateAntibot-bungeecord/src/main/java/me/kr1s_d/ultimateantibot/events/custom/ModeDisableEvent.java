package me.kr1s_d.ultimateantibot.events.custom;


import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.user.PlayerProfile;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.plugin.Event;

import java.util.ArrayList;
import java.util.List;

public class ModeDisableEvent extends Event {

    private final IAntiBotManager antiBotManager;
    private final ModeType enabledMode;
    private final UserDataService userDataService;

    public ModeDisableEvent(IAntiBotPlugin antiBotPlugin, ModeType modeType){
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.enabledMode = modeType;
        this.userDataService = antiBotPlugin.getUserDataService();
    }

    public ModeType getEnabledMode() {
        return enabledMode;
    }

    public void disconnectBots(){
        List<PlayerProfile> profileList = new ArrayList<>();
        antiBotManager.getJoinCache().getJoined().forEach(a -> profileList.add(userDataService.getFromIP(a)));
        profileList.forEach(userDataService::resetFirstJoin);
        Utils.disconnectAll(antiBotManager.getJoinCache().getJoined(), MessageManager.getSafeModeMessage());
        antiBotManager.getJoinCache().clear();
    }
}
