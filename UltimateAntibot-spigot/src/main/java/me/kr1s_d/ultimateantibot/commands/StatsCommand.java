package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class StatsCommand implements SubCommand {

    private final IAntiBotPlugin iAntiBotPlugin;

    public StatsCommand(IAntiBotPlugin iAntiBotPlugin){
        this.iAntiBotPlugin = iAntiBotPlugin;
    }

    @Override
    public String getSubCommandId() {
        return "stats";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§8§l§n___________________________________________");
        sender.sendMessage("");
        sender.sendMessage("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + iAntiBotPlugin.getVersion());
        MessageManager.statsMessage.forEach(a -> sender.sendMessage(Utils.colora(iAntiBotPlugin.getAntiBotManager().replaceInfo(a))));
        sender.sendMessage("§8§l§n___________________________________________");
    }

    @Override
    public String getPermission() {
        return "uab.command.stats";
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
