package me.kr1s_d.ultimateantibot.events.custom;


import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import net.md_5.bungee.api.plugin.Event;

public class ModeDisableEvent extends Event {

    private IAntiBotPlugin antiBotPlugin;
    private IAntiBotManager antiBotManager;
    private ModeType enabledMode;
    private UserDataService userDataService;

    public ModeDisableEvent(IAntiBotPlugin antiBotPlugin, ModeType modeType){
        this.antiBotPlugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.enabledMode = modeType;
        this.userDataService = antiBotPlugin.getUserDataService();
    }

    public ModeType getEnabledMode() {
        return enabledMode;
    }

    public void disconnectBots(){

    }
}
