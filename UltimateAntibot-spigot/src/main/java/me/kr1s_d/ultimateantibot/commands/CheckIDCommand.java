package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        TextComponent whitelistComponent = new TextComponent();
        whitelistComponent.setText("§a§l[WHITELIST]§f");
        whitelistComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§a§n» Click to WHITELIST this IP!").create())));
        whitelistComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uab whitelist add " + profile.getIp().replace("/", "")));

        TextComponent blacklistComponent = new TextComponent();
        blacklistComponent.setText("§c§l[BLACKLIST]§f");
        blacklistComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§c§n» Click to BLACKLIST this IP!").create())));
        blacklistComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uab blacklist add " + profile.getIp().replace("/", "")));

        ((Player)sender).spigot().sendMessage(whitelistComponent, blacklistComponent);
    }

    @Override
    public String getPermission() {
        return "uab.command.check";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Collections.singletonList("<id to check>"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return false;
    }
}
