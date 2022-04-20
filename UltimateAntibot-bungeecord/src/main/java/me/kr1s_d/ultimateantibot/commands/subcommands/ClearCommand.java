package me.kr1s_d.ultimateantibot.commands.subcommands;

import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import java.util.*;

public class ClearCommand implements SubCommand {

    private final IAntiBotManager antiBotManager;

    public ClearCommand(IAntiBotPlugin iAntiBotPlugin){
        antiBotManager = iAntiBotPlugin.getAntiBotManager();
    }

    @Override
    public String getSubCommandId() {
        return "clear";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args[1].equals("whitelist")){
            antiBotManager.getWhitelistService().clear();
            sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.getCommandCleared("whiteList")));
        }else{
            if(args[1].equals("blacklist")){
                antiBotManager.getBlackListService().clear();
                sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.getCommandCleared("blackList")));
            }else{
                sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.commandWrongArgument));
            }
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.clear";
    }

    @Override
    public int argsSize() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("whitelist", "blacklist"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
