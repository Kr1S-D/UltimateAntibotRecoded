package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.utils.NotificationUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class commands extends Command {

    private IAntiBotPlugin plugin;

    public commands(IAntiBotPlugin plugin) {
        super("ultimateantibot", "", "uab");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        NotificationUtils.toggleActionBar((ProxiedPlayer) sender);
    }
}
