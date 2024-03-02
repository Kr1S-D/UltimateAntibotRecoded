package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.attack.AttackLog;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

/**
 * /uab logs list <amount>
 * /uab logs info <id>
 */
public class AttackLogCommand implements SubCommand {
    private final IAntiBotPlugin plugin;

    public AttackLogCommand(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getSubCommandId() {
        return "logs";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int value = -1;

        try {
            value = Integer.parseInt(args[2]);
        }catch (Exception e) {
            value = 1;
        }

        if(args[1].equals("info")) {
            Optional<AttackLog> log = plugin.getAttackTrackerService().getAttackLog(value);
            if (!log.isPresent()) {
                sender.sendMessage(Utils.colora(MessageManager.getMessage("commands.invalid-log-value")));
                return;
            }

            sender.sendMessage("§8§l§n___________________________________________");
            sender.sendMessage("");
            sender.sendMessage("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + plugin.getVersion());
            AttackLog attackLog = log.get();
            List<String> messages = MessageManager.getMessageList("attack-log").stream().map(attackLog::replaceInformation).map(Utils::colora).collect(Collectors.toList());
            messages.forEach(sender::sendMessage);
            sender.sendMessage("§8§l§n___________________________________________");
        }

        if(args[1].equals("list")) {
            sender.sendMessage(Utils.colora(MessageManager.prefix + "&fHere is a list of the last &c" + value + " &fattacks"));
            List<AttackLog> lastAttacks = plugin.getAttackTrackerService().getLastAttacks(value);
            if(lastAttacks.size() == 0) {
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fThere are no attacks to show!"));
                return;
            }
            for (AttackLog attack : lastAttacks) {
                TextComponent component = new TextComponent(Utils.colora("&c" + attack.getAttackDate() + " &7-> &f" + attack.getID() + " &7(&c" + attack.getAttackPower() +"&7) &7(&o&nHover me!&7)"));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a§n» Click to see more details!").create()));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/uab logs info " + attack.getID()));
                sender.spigot().sendMessage(component);
            }
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.logs";
    }

    @Override
    public int minArgs() {
        return 3;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("list", "info"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return false;
    }
}
