package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.UltimateAntiBotSpigot;
import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.server.SatelliteServer;
import me.kr1s_d.ultimateantibot.common.objects.server.json.StatusResponseJSON;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

/*
public class SatelliteCommand implements SubCommand {

    private final IAntiBotPlugin iAntiBotPlugin;
    private final SatelliteServer satelliteServer;

    public SatelliteCommand(IAntiBotPlugin iAntiBotPlugin){
        this.iAntiBotPlugin = iAntiBotPlugin;
        this.satelliteServer = iAntiBotPlugin.getSatellite();
    }

    @Override
    public String getSubCommandId() {
        return "satellite";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ColorHelper.colorize(MessageManager.prefix + "&7Sending request to UAB servers, please wait..."));
        UltimateAntiBotSpigot.getInstance().runTask(
                () -> {
                    long a = System.currentTimeMillis();
                    StatusResponseJSON responseJSON = satelliteServer.getStatus();
                    long b = System.currentTimeMillis() - a;
                    if(responseJSON == null){
                        sender.sendMessage(ColorHelper.colorize(MessageManager.prefix + "&fError requesting &cUAB&f servers, are they &coffline?"));
                        return;
                    }
                    sender.sendMessage("§8§l§n___________________________________________");
                    sender.sendMessage("");
                    sender.sendMessage("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + iAntiBotPlugin.getVersion());
                    responseJSON.getStatusMessage(b).forEach(c -> sender.sendMessage(Utils.colora(c)));
                    sender.sendMessage("§8§l§n___________________________________________");
                },
                true
        );
    }

    @Override
    public String getPermission() {
        return "uab.command.satellite";
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
*/
