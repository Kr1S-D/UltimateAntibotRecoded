package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class InvalidNameCheck implements JoinCheck {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final List<String> invalidNames;
    
    public InvalidNameCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.invalidNames = ConfigManger.invalidNamesBlockedEntries;

        if(isEnabled()){
            loadTask();
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        for (String patternString : invalidNames) {
            Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER_INVALID_NAME, name);
                plugin.getLogHelper().debug("[UAB DEBUG] Detected attack on InvalidNameCheck!");
                return true;
            }
        }
        
        return false;
    }

    @Override
    public CheckType getType() {
        return CheckType.BLACKLISTED_NAME;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isInvalidNameCheckEnabled;
    }

    @Override
    public long getCacheSize() {
        return -1;
    }

    @Override
    public void clearCache() {
        //NOT SUPPORTED HERE
    }

    @Override
    public void removeCache(String ip) {
        //NOT SUPPORTED HERE
    }

    public void loadTask() {
        //USELESS
    }
}
