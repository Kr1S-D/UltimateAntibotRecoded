package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class ReloadCommand implements SubCommand {
    private final IAntiBotPlugin plugin;

    public ReloadCommand(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getSubCommandId() {
        return "reload";
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        plugin.reload();
        commandSender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.reloadMessage));
    }

    @Override
    public String getPermission() {
        return "uab.command.reload";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
