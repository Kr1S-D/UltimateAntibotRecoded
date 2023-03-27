package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.List;

public class FirstJoinCheck implements JoinCheck {
    private final UserDataService userDataService;

    public FirstJoinCheck(IAntiBotPlugin plugin){
        this.userDataService = plugin.getUserDataService();

        if(isEnabled()){
            loadTask();
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    /**
     * @param ip - ip of the player
     * @param name - name of the player
     * @return - false if player can join or true if is first join
     */
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()){
            return false;
        }

        return userDataService.isFirstJoin(ip);
    }

    @Override
    public CheckType getType() {
        return CheckType.FIRST_JOIN;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    public boolean isEnabled() {
        return ConfigManger.isFirstJoinEnabled;
    }

    @Override
    public long getCacheSize() {
        return userDataService.size();
    }

    @Override
    public void clearCache() {
        //not supported here
    }

    @Override
    public void removeCache(String ip) {
        //NOT SUPPORTED HERE
    }

    public void loadTask() {

    }
}
