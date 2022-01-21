package me.kr1s_d.ultimateantibot.commands.subcommands;

import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.NotificationUtils;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToggleNotificationCommand implements SubCommand {

    @Override
    public String getSubCommandId() {
        return "toggle";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args[1].equals("actionbar")) {
            NotificationUtils.toggleActionBar((ProxiedPlayer) sender);
        } else {
            if (args[1].equals("title")) {
                NotificationUtils.toggleTitle((ProxiedPlayer) sender);
            } else {
                if (args[1].equals("chat")) {
                    NotificationUtils.toggleChat((ProxiedPlayer) sender);
                } else {
                    sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.commandWrongArgument));
                }
            }
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.toggle";
    }

    @Override
    public int argsSize() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("actionbar", "title", "chat"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return false;
    }
}
