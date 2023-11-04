package me.kr1s_d.ultimateantibot.commands;

import com.velocitypowered.api.command.CommandSource;

import java.util.List;
import java.util.Map;

public interface SubCommand {
    String getSubCommandId();

    void execute(LegacyCommandSource sender, String[] args);

    String getPermission();

    int argsSize();

    Map<Integer, List<String>> getTabCompleter();

    boolean allowedConsole();
}
