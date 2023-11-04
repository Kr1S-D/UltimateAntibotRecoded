package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.kyori.adventure.text.Component;

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
    public void execute(CommandSource sender, String[] args) {
        sender.sendMessage(Component.text("§8§l§n___________________________________________"));
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Component.text("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + iAntiBotPlugin.getVersion()));
        MessageManager.helpMessage.forEach(a -> sender.sendMessage(Utils.colora(a)));
        sender.sendMessage(Component.text("§8§l§n___________________________________________"));
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
