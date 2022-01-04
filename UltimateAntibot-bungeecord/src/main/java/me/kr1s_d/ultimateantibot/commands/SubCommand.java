package me.kr1s_d.ultimateantibot.commands;

import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.Map;

public interface SubCommand {
    String getSubCommandId();

    void execute(CommandSender sender, String[] args);

    String getPermission();

    int argsSize();

    Map<Integer, List<String>> getTabCompleter();

    boolean allowedConsole();
}
