package me.kr1s_d.ultimateantibot.commands.subcommands;

import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import java.util.*;

public class AddRemoveBlacklistCommand implements SubCommand {

    private final IAntiBotManager iAntiBotManager;

    public AddRemoveBlacklistCommand(IAntiBotPlugin iAntiBotPlugin){
        this.iAntiBotManager = iAntiBotPlugin.getAntiBotManager();
    }

    @Override
    public String getSubCommandId() {
        return "blacklist";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase("add")) {
            iAntiBotManager.getBlackListService().blacklist("/" + args[2], BlackListReason.ADMIN);
            iAntiBotManager.getWhitelistService().unWhitelist("/" + args[2]);
            sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.getCommandAdded(args[2], "blacklist")));
        } else {
            if (args[1].equalsIgnoreCase("remove")) {
                iAntiBotManager.getBlackListService().unBlacklist( "/" + args[2]);
                sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.getCommandRemove(args[2], "blacklist")));
            } else {
                sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.commandWrongArgument));
            }
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.blacklist";
    }

    @Override
    public int argsSize() {
        return 3;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("add", "remove"));
        map.put(2, Collections.singletonList("<Ip address to blacklist>"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
