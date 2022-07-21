package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;

public class LegalNameCheck implements ICheck {
    private static final String VALID_NAME = "[a-zA-Z0-9_]*";

    private final IAntiBotManager antiBotManager;

    public LegalNameCheck(IAntiBotPlugin plugin) {
        this.antiBotManager = plugin.getAntiBotManager();
    }

    @Override
    public boolean isDenied(String ip, String name) {
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
        return true;
    }

    @Override
    public void loadTask() {

    }
}
