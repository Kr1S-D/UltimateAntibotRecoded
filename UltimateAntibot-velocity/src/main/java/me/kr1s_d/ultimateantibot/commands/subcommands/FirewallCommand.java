package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.FirewallService;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.kyori.adventure.text.Component;

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
    public void execute(CommandSource sender, String[] args) {
        FirewallService service = iAntiBotPlugin.getFirewallService();
        sender.sendMessage(Component.text("§8§l§n___________________________________________"));
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Component.text("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + iAntiBotPlugin.getVersion()));
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Utils.colora("&cFirewall status: &f%status%".replace("%status%", service.getFirewallStatus())));
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Utils.colora("&cIP in queue:&f %queue%".replace("%queue%", String.valueOf(service.getIPQueue()))));
        sender.sendMessage(Utils.colora("&cIP blacklisted:&f %blacklist%".replace("%blacklist%", String.valueOf(service.getBlacklistedIP()))));
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Utils.colora("&cThe attack will be mitigated when all IPs have been blocked!"));
        sender.sendMessage(Component.text("§8§l§n___________________________________________"));
    }

    @Override
    public String getPermission() {
        return "uab.command.firewall";
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
