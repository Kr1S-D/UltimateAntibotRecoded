package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.FirewallService;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class FirewallCommand implements SubCommand {
    private final IAntiBotPlugin iAntiBotPlugin;

    public FirewallCommand(IAntiBotPlugin iAntiBotPlugin) {
        this.iAntiBotPlugin = iAntiBotPlugin;
    }

    @Override
    public String getSubCommandId() {
        return "firewall";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        FirewallService service = iAntiBotPlugin.getFirewallService();

        sender.sendMessage("§8§l§n___________________________________________");
        sender.sendMessage("");
        sender.sendMessage("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + iAntiBotPlugin.getVersion());
        sender.sendMessage("");
        sender.sendMessage(Utils.colora("&cFirewall status: &f%status%".replace("%status%", service.getFirewallStatus())));
        sender.sendMessage("");
        sender.sendMessage(Utils.colora("&cIP in queue:&f %queue%".replace("%queue%", String.valueOf(service.getIPQueue()))));
        sender.sendMessage(Utils.colora("&cIP blacklisted:&f %blacklist%").replace("%blacklist%", String.valueOf(service.getBlacklistedIP())));
        sender.sendMessage("");
        sender.sendMessage(Utils.colora("&cThe attack will be mitigated when all IPs have been blocked!"));
        sender.sendMessage("§8§l§n___________________________________________");
    }

    @Override
    public String getPermission() {
        return "uab.command.firewall";
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
