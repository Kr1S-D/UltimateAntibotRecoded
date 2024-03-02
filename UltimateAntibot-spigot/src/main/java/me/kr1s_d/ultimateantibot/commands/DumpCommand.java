package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.PasteBinBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class DumpCommand implements SubCommand {

    private final IAntiBotPlugin plugin;

    public DumpCommand(IAntiBotPlugin iAntiBotManager){
        this.plugin = iAntiBotManager;
    }

    @Override
    public String getSubCommandId() {
        return "dump";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PasteBinBuilder pasteBinBuilder = PasteBinBuilder.builder();
        pasteBinBuilder.addLine("Name: " + Bukkit.getName());
        pasteBinBuilder.addLine("Version: " + Bukkit.getVersion());
        pasteBinBuilder.addLine("Online Count: " + Bukkit.getOnlinePlayers().size());
        pasteBinBuilder.addLine("Plugins:");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getDescription().getName().contains("Protocol") || plugin.getDescription().getName().contains("Geyser")) {
                pasteBinBuilder.addLine(plugin.getDescription().getName() + " - " + plugin.getDescription().getVersion() + " - It could cause problems!");
            } else {
                pasteBinBuilder.addLine(plugin.getDescription().getName() + " - " + plugin.getDescription().getVersion());
            }
        }
        pasteBinBuilder.addLine("Plugin Info:");
        pasteBinBuilder.addLine("Version: " + plugin.getVersion());
        pasteBinBuilder.addLine("Whitelist Size: " + plugin.getAntiBotManager().getWhitelistService().size());
        pasteBinBuilder.addLine("Blacklist Size: " + plugin.getAntiBotManager().getBlackListService().size());
        pasteBinBuilder.addLine("Users: " + plugin.getUserDataService().size());
        plugin.runTask(pasteBinBuilder::pasteSync, true);
        pasteBinBuilder.pasteSync();
        sender.sendMessage(Utils.colora(MessageManager.prefix + "&fsending request to server, wait please..."));
        plugin.scheduleDelayedTask(() -> {
            if (pasteBinBuilder.isReady()) {
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fDump is ready: " + pasteBinBuilder.getUrl() + " &7(It will reset in a week!)"));
            }else{
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fUnable to complete request, try later..."));
            }
        }, true, 2500L);
    }

    @Override
    public String getPermission() {
        return "uab.command.dump";
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
