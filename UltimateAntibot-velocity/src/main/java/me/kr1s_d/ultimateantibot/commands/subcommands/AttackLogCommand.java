package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.attack.AttackLog;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.component.KComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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
    public void execute(CommandSource sender, String[] args) {
        int value = -1;

        try {
            value = Integer.parseInt(args[2]);
        }catch (Exception e) {
            value = -1;
        }

        if(args[1].equals("info")) {
            Optional<AttackLog> log = plugin.getAttackTrackerService().getAttackLog(value);
            if (!log.isPresent()) {
                sender.sendMessage(Utils.colora(MessageManager.getMessage("commands.invalid-log-value")));
                return;
            }

            sender.sendMessage(Component.text("§8§l§n___________________________________________"));
            sender.sendMessage(Component.text(""));
            sender.sendMessage(Component.text("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + plugin.getVersion()));
            AttackLog attackLog = log.get();
            List<Component> messages = MessageManager.getMessageList("attack-log").stream().map(attackLog::replaceInformation).map(Utils::colora).collect(Collectors.toList());
            messages.forEach(sender::sendMessage);
            sender.sendMessage(Component.text("§8§l§n___________________________________________"));
        }

        if(args[1].equals("list")) {
            sender.sendMessage(Utils.colora(MessageManager.prefix + "&fHere is a list of the last &c" + value + " &fattacks"));
            List<AttackLog> lastAttacks = plugin.getAttackTrackerService().getLastAttacks(value);
            if(lastAttacks.size() == 0) {
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fThere are no attacks to show!"));
                return;
            }
            for (AttackLog attack : lastAttacks) {
                KComponentBuilder.interact("&c" + attack.getAttackDate() + " &7-> &f" + attack.getID() + " &7(&c" + attack.getAttackPower() +"&7) &7(&o&nHover me!&7)")
                        .hover(HoverEvent.Action.SHOW_TEXT, "§a§n» Click to see more details!")
                        .click(ClickEvent.Action.SUGGEST_COMMAND, "/uab logs info  + attack.getID()")
                        .send((Player) sender);
            }
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.logs";
    }

    @Override
    public int argsSize() {
        return 3;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("list", "info"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return false;
    }
}
