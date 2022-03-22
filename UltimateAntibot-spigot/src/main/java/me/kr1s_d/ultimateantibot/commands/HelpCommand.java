package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class HelpCommand implements SubCommand {

    private final IAntiBotPlugin iAntiBotPlugin;

    public HelpCommand(IAntiBotPlugin iAntiBotPlugin){
        this.iAntiBotPlugin = iAntiBotPlugin;
    }

    @Override
    public String getSubCommandId() {
        return "help";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§8§l§n___________________________________________");
        sender.sendMessage("");
        sender.sendMessage("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + iAntiBotPlugin.getVersion());
        MessageManager.helpMessage.forEach(a -> sender.sendMessage(Utils.colora(a)));
        sender.sendMessage("§8§l§n___________________________________________");
    }

    @Override
    public String getPermission() {
        return "uab.command.help";
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
