package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.Notificator;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.Version;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            Notificator.toggleActionBar((Player) sender);
        } else {
            if (args[1].equals("title")) {
                Notificator.toggleTitle((Player) sender);
            } else {
                if (args[1].equals("bossbar")) {
                    if(Version.getBukkitServerVersion() < 19){
                        sender.sendMessage(Utils.colora(MessageManager.prefix + "&cBossbar&f notifications are not supported on &c1.8.x!"));
                        return;
                    }
                    Notificator.toggleBossBar((Player) sender);
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
    public int minArgs() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("actionbar", "title", "bossbar"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return false;
    }
}

