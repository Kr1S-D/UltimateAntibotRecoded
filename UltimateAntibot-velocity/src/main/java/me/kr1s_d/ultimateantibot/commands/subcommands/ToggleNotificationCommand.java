package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.commands.LegacyCommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;

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
    public void execute(LegacyCommandSource sender, String[] args) {
        if (args[1].equals("actionbar")) {
            Notificator.toggleActionBar((Player) sender);
        } else {
            if (args[1].equals("title")) {
                Notificator.toggleTitle((Player) sender);
            } else {
                if (args[1].equals("bossbar")) {
                    if(((Player) sender).getProtocolVersion().getProtocol() > 106) {
                        Notificator.toggleBossBar((Player) sender);
                    }else {
                        sender.sendMessage(Utils.colora(MessageManager.prefix + "&cBossbar&f notifications are not supported on &c1.8.x!"));
                    }
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
        map.put(1, Arrays.asList("actionbar", "title", "bossbar"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return false;
    }
}
