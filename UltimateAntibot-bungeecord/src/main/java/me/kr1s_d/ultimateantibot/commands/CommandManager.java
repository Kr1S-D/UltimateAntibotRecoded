package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends Command implements TabExecutor {

    private final List<SubCommand> loadedCommands;
    private final List<String> tabComplete;
    private String defaultCommandWrongArgumentMessage;
    private String noPermsMessage;
    private String noPlayerMessage;

    public CommandManager(IAntiBotPlugin iAntiBotPlugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.loadedCommands = new ArrayList<>();
        this.tabComplete = new ArrayList<>();
        this.defaultCommandWrongArgumentMessage = MessageManager.commandWrongArgument;
        this.noPermsMessage  = MessageManager.commandNoPerms;
        this.noPlayerMessage = "&7You are not a &dPlayer!";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0){
            sender.sendMessage(Utils.colora(MessageManager.prefix + defaultCommandWrongArgumentMessage));
            return;
        }
        SubCommand cmd = getSubCommandFromArgs(args[0]);
        if(cmd == null){
            sender.sendMessage(Utils.colora(MessageManager.prefix + defaultCommandWrongArgumentMessage));
            return;
        }
        if (args[0].equals(cmd.getSubCommandId()) && args.length == cmd.argsSize()) {
            if (!cmd.allowedConsole() && !(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(Utils.colora(MessageManager.prefix + noPlayerMessage));
                return;
            }
            if (sender.hasPermission(cmd.getPermission())) {
                cmd.execute(sender, args);
            } else {
                sender.sendMessage(Utils.colora(MessageManager.prefix + noPermsMessage));
            }
        } else {
            sender.sendMessage(Utils.colora(MessageManager.prefix + defaultCommandWrongArgumentMessage));
        }
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        SubCommand subCommand = getSubCommandFromArgs(args[0]);
        if (subCommand != null && args[0].equals(subCommand.getSubCommandId())) {
            if (subCommand.getTabCompleter() != null) {
                return subCommand.getTabCompleter().get(args.length - 1);
            }else{
                return new ArrayList<>();
            }
        }
        if (args.length == 1) {
            return tabComplete;
        }
        return new ArrayList<>();
    }

    public void setDefaultCommandWrongArgumentMessage(String defaultCommandWrongArgumentMessage) {
        this.defaultCommandWrongArgumentMessage = defaultCommandWrongArgumentMessage;
    }

    public void setNoPlayerMessage(String noPlayerMessage) {
        this.noPlayerMessage = noPlayerMessage;
    }

    public void setNoPermsMessage(String noPermsMessage) {
        this.noPermsMessage = noPermsMessage;
    }

    private SubCommand getSubCommandFromArgs(String args0){
        for(SubCommand subCommand : loadedCommands){
            if(subCommand.getSubCommandId().equals(args0)){
                return subCommand;
            }
        }
        return null;
    }

    public void register(SubCommand subCommand){
        loadedCommands.add(subCommand);
        tabComplete.add(subCommand.getSubCommandId());

    }
}
