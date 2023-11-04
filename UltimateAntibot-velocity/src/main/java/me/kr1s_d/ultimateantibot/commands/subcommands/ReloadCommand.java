package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.commands.LegacyCommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;

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
    public void execute(LegacyCommandSource sender, String[] strings) {
        plugin.reload();
        sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.reloadMessage));
    }

    @Override
    public String getPermission() {
        return "uab.command.reload";
    }

    @Override
    public int argsSize() {
        return 1;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        return null;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
