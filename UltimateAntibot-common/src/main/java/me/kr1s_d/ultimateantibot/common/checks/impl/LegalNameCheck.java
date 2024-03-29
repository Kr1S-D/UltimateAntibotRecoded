package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.JoinCheck;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class LegalNameCheck implements JoinCheck {
    private static String VALID_NAME_REGEX = "[a-zA-Z0-9_.]*";

    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;

    public LegalNameCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();

        if(ConfigManger.legalNameCheckRegex != null) {
            VALID_NAME_REGEX = ConfigManger.legalNameCheckRegex;
        }

        if(isEnabled()){
            CheckService.register(this);
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()) return false;
        if(name.matches(VALID_NAME_REGEX) && name.length() <= 16){
            return false;
        }

        antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER_INVALID_NAME, "_INVALID_");
        plugin.getLogHelper().debug("[UAB DEBUG] Detected bot on LegalNameCheck!");
        return true;
    }

    @Override
    public CheckType getType() {
        return CheckType.LEGAL_NAME;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isLegalNameCheckEnabled;
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
}
