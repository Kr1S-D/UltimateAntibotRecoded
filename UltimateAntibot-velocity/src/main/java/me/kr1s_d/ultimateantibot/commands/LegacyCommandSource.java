package me.kr1s_d.ultimateantibot.commands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.utils.Utils;

public abstract class LegacyCommandSource implements CommandSource {
    public void sendMessage(String str) {
        sendMessage(Utils.colora(str));
    }
}
