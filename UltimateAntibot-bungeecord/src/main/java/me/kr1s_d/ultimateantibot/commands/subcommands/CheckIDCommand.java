package me.kr1s_d.ultimateantibot.commands.subcommands;

import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.base.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckIDCommand implements SubCommand {

    private final IAntiBotPlugin plugin;

    public CheckIDCommand(IAntiBotPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getSubCommandId() {
        return "check";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BlackListProfile profile = plugin.getAntiBotManager().getBlackListService().getBlacklistProfileFromID(args[1]);
        if(profile == null){
            sender.sendMessage(Utils.colora(MessageManager.prefix + MessageManager.commandNoBlacklist));
            return;
        }
        for(String str : MessageManager.blacklistProfileString){
            sender.sendMessage(Utils.colora(str
                    .replace("$reason", profile.getReason())
                    .replace("$id", profile.getId())
                    .replace("$nick", profile.getName())
                    .replace("$ip", profile.getIp())
            ));
        }

    }

    @Override
    public String getPermission() {
        return "uab.command.check";
    }

    @Override
    public int argsSize() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Collections.singletonList("<id to check>"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
