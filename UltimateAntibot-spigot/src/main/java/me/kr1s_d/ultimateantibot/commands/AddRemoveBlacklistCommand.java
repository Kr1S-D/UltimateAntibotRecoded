package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.NameIPEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.mapping.IPMapping;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.StringUtil;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class AddRemoveBlacklistCommand implements SubCommand {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager iAntiBotManager;

    public AddRemoveBlacklistCommand(IAntiBotPlugin iAntiBotPlugin) {
        this.plugin = iAntiBotPlugin;
        this.iAntiBotManager = iAntiBotPlugin.getAntiBotManager();
    }

    @Override
    public String getSubCommandId() {
        return "blacklist";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //name mapping execution
        IPMapping ipMapping = iAntiBotManager.getBlackListService().getIPMapping();
        String ipFromName = ipMapping.getIPFromName(args[2]);
        if (ipFromName != null) {
            args[2] = ipFromName;
        }

        if (args[1].equalsIgnoreCase("add") && !StringUtil.isValidIPv4(args[2])) {
            ConnectionProfile connectionProfile = plugin.getUserDataService().getConnectedProfiles().stream()
                    .filter(s -> s.getCurrentNickName().equalsIgnoreCase(args[2]))
                    .findAny()
                    .orElse(null);

            if (connectionProfile != null) {
                args[2] = connectionProfile.getIP();
            }
        }

        if (!StringUtil.isValidIPv4(args[2])) {
            sender.sendMessage(Utils.colora(MessageManager.prefix + "&FThe IP entered is invalid or the player's name is not present in the database"));
            return;
        }

        //remove slash
        args[2] = args[2].replace("/", "");

        if (args[1].equalsIgnoreCase("add")) {
            iAntiBotManager.getBlackListService().blacklist("/" + args[2], BlackListReason.ADMIN, plugin.getUserDataService().getProfile("/" + args[2]).getCurrentNickName());
            iAntiBotManager.getWhitelistService().unWhitelist("/" + args[2]);
            plugin.disconnect("/" + args[2], MessageManager.getBlacklistedMessage(iAntiBotManager.getBlackListService().getProfile("/" + args[2])));
            sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.getCommandAdded(args[2], "blacklist")));
            sender.sendMessage(Utils.colora(MessageManager.prefix + "&7PS: The IP has been removed from the whitelist as it has been blacklisted!"));
        } else {
            if (args[1].equalsIgnoreCase("remove")) {
                iAntiBotManager.getBlackListService().unBlacklist("/" + args[2]);
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
    public int minArgs() {
        return 3;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("add", "remove"));
        map.put(2, Collections.singletonList("<Ip address/Player Name>"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
