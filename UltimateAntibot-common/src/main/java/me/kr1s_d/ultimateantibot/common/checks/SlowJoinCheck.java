package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.server.packet.VerificationPacket;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.List;

public class SlowJoinCheck implements JoinCheck {
    private final IAntiBotPlugin plugin;
    private final List<String> lastNames;

    public SlowJoinCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.lastNames = new ArrayList<>();
    }

    @Override
    public boolean isDenied(String ip, String name) {
        return false;
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
        plugin.scheduleRepeatingTask(lastNames::clear, false, 1000L * 60L);
    }
}
