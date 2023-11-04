package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;
import me.kr1s_d.ultimateantibot.commands.LegacyCommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.PasteBinBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;

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
    public void execute(LegacyCommandSource sender, String[] args) {
        PasteBinBuilder pasteBinBuilder = PasteBinBuilder.builder();
        pasteBinBuilder.addLine("Name: " + UltimateAntiBotVelocity.getInstance().getServer().getVersion().getName());
        pasteBinBuilder.addLine("Version: " + UltimateAntiBotVelocity.getInstance().getServer().getVersion().getVersion());
        pasteBinBuilder.addLine("Online Count: " + Utils.calculatePlayerNames().size());
        pasteBinBuilder.addLine("Plugins:");
        for (PluginContainer plugin : UltimateAntiBotVelocity.getInstance().getServer().getPluginManager().getPlugins()) {
            if (plugin.getDescription().getName().orElse("").contains("Protocol") || plugin.getDescription().getName().orElse("").contains("Geyser")) {
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
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fDump is ready: &n" + pasteBinBuilder.getUrl() + "&f &7(It will reset in a week!)"));
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
