package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.commandframework.utils.CommandMapBuilder;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CacheCommand implements SubCommand {
    @Override
    public String getSubCommandId() {
        return "cache";
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        switch (strings[1]) {
            case "status":
                for (String s : CheckService.getInformationAsMessage()) {
                    commandSender.sendMessage(Utils.colora(s));
                }
            case "clear":
                CheckService.clearCheckCache();
                commandSender.sendMessage(Utils.colora(MessageManager.prefix + "&fThe &c&lUAB &fcache has been cleared!"));
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.cache";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        return CommandMapBuilder.builder()
                .set(1, Arrays.asList("status", "clear"))
                .getMap();
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
