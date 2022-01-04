package me.kr1s_d.ultimateantibot.commands.subcommands;

import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.PasteBinBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
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
        pasteBinBuilder.addLine("Name: " + ProxyServer.getInstance().getName());
        pasteBinBuilder.addLine("Version: " + ProxyServer.getInstance().getVersion());
        pasteBinBuilder.addLine("Online Count: " + ProxyServer.getInstance().getOnlineCount());
        pasteBinBuilder.addLine("Plugins:");
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
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
        pasteBinBuilder.pasteAsync();
        sender.sendMessage(Utils.colora(MessageManager.prefix + "&fsending request to server, wait please..."));
        plugin.scheduleDelayedTask(() -> {
            if (pasteBinBuilder.isReady()) {
                sender.sendMessage(new TextComponent(Utils.colora(MessageManager.prefix + "&fDump is ready: " + pasteBinBuilder.getUrl() + " &7(It will reset in a week!)")));
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
