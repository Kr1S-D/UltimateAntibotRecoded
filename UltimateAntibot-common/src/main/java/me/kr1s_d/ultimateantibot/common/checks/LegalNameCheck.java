package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class LegalNameCheck implements ICheck {
    private static String VALID_NAME = "[a-zA-Z0-9_.]*";

    private final IAntiBotManager antiBotManager;

    public LegalNameCheck(IAntiBotPlugin plugin) {
        this.antiBotManager = plugin.getAntiBotManager();

        if(ConfigManger.legalNameCheckRegex != null) {
            VALID_NAME = ConfigManger.legalNameCheckRegex;
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()) return false;
        if(name.matches(VALID_NAME) && name.length() <= 16){
            return false;
        }

        antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER, "_INVALID_");
        return true;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isLegalNameCheckEnabled;
    }

    @Override
    public void loadTask() {

    }
}
