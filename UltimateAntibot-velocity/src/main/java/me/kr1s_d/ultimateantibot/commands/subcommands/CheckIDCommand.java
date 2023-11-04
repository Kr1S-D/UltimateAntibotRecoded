package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.component.KComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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
    public void execute(CommandSource sender, String[] args) {
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

        TextComponent whitelistComponent = KComponentBuilder.interact("§a§l[WHITELIST]§f")
                .hover(HoverEvent.Action.SHOW_TEXT, "§a§n» Click to WHITELIST this IP!")
                .click(ClickEvent.Action.SUGGEST_COMMAND, "/uab whitelist add " + profile.getIp().replace("/", ""))
                .getComponent();

        TextComponent blacklistComponent = KComponentBuilder.interact("§c§l[BLACKLIST]§f")
                .hover(HoverEvent.Action.SHOW_TEXT, "§c§n» Click to BLACKLIST this IP!")
                .click(ClickEvent.Action.SUGGEST_COMMAND, "/uab blacklist add " + profile.getIp().replace("/", ""))
                .getComponent();

        sender.sendMessage(whitelistComponent);
        sender.sendMessage(blacklistComponent);
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
        return false;
    }
}
