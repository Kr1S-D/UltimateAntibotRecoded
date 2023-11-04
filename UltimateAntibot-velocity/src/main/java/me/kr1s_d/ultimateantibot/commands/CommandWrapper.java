package me.kr1s_d.ultimateantibot.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandWrapper implements SimpleCommand {
    private final List<SubCommand> loadedCommands;
    private final List<String> tabComplete;
    private String defaultCommandWrongArgumentMessage;
    private String noPermsMessage;
    private String noPlayerMessage;

    public CommandWrapper(IAntiBotPlugin iAntiBotPlugin) {
        this.loadedCommands = new ArrayList<>();
        this.tabComplete = new ArrayList<>();
        this.defaultCommandWrongArgumentMessage = MessageManager.commandWrongArgument;
        this.noPermsMessage  = MessageManager.commandNoPerms;
        this.noPlayerMessage = "&7You are not a &cplayer!";
    }

    @Override
    public void execute(Invocation invocation) {
        com.velocitypowered.api.command.CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if(args.length == 0){
            sender.sendMessage(Utils.colora(MessageManager.prefix + defaultCommandWrongArgumentMessage));
            return;
        }
        SubCommand cmd = getSubCommandFromArgs(args[0]);
        if(cmd == null){
            sender.sendMessage(Utils.colora(MessageManager.prefix + defaultCommandWrongArgumentMessage));
            return;
        }
        if (args[0].equals(cmd.getSubCommandId()) && args.length >= cmd.argsSize()) {
            if (!cmd.allowedConsole() && !(sender instanceof Player)) {
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
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();

        if(args.length == 0) return getPrimarySubCommands();
        SubCommand subCommand = getSubCommandFromArgs(args[0]);
        if (subCommand != null && args[0].equals(subCommand.getSubCommandId())) {
            if (subCommand.getTabCompleter() != null && subCommand.getTabCompleter().get(args.length - 1) != null) {
                return subCommand.getTabCompleter().get(args.length - 1);
            }else{
                return Utils.calculatePlayerNames();
            }
        }
        if (args.length == 1) {
            return tabComplete;
        }

        return Utils.calculatePlayerNames();
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

    private List<String> getPrimarySubCommands() {
        return loadedCommands.stream().map(SubCommand::getSubCommandId).collect(Collectors.toList());
    }

    public void register(SubCommand subCommand){
        loadedCommands.add(subCommand);
        tabComplete.add(subCommand.getSubCommandId());
    }
}
