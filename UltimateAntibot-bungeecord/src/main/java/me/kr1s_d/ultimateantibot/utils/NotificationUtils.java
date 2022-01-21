package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtils {

    private NotificationUtils(){}

    private static final List<ProxiedPlayer> actionbars = new ArrayList<>();
    private static final List<ProxiedPlayer> titles = new ArrayList<>();
    private static final List<ProxiedPlayer> chat = new ArrayList<>();

    public static void toggleActionBar(ProxiedPlayer player){
        if(actionbars.contains(player)){
            actionbars.remove(player);
        }else {
            actionbars.add(player);
        }
        player.sendMessage(new TextComponent(ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledActionbar)));
    }

    public static void toggleTitle(ProxiedPlayer player){
        if(titles.contains(player)){
            titles.remove(player);
        }else {
            titles.add(player);
        }
        player.sendMessage(new TextComponent(ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledTitle)));
    }

    public static void toggleChat(ProxiedPlayer player){
        if(chat.contains(player)){
            chat.remove(player);
        }else {
            chat.add(player);
        }
        player.sendMessage(new TextComponent(ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledChat)));
    }

    public static void sendActionbar(String str){
        actionbars.forEach(ac -> ac.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorHelper.colorize(str))));
    }

    public static void sendTitle(String title, String subtitle){
        Title t = ProxyServer.getInstance().createTitle();
        t.title(new TextComponent(ColorHelper.colorize(title)));
        t.subTitle(new TextComponent(ColorHelper.colorize(subtitle)));
        titles.forEach(t::send);
    }

    public static void sendChatMessage(String str){
        chat.forEach(player -> player.sendMessage(new TextComponent(ColorHelper.colorize(str))));
    }

    public static void update(IAntiBotPlugin plugin){
        plugin.scheduleRepeatingTask(() -> {
            if(plugin.getAntiBotManager().isSomeModeOnline()){
                sendTitle(MessageManager.titleTitle, plugin.getAntiBotManager().replaceInfo(MessageManager.titleSubtitle));
            }
            if(plugin.getAntiBotManager().isPacketModeEnabled()){
                sendActionbar(MessageManager.prefix + plugin.getAntiBotManager().replaceInfo(MessageManager.actionbarPackets));
                return;
            }
            if(plugin.getAntiBotManager().isSomeModeOnline()) {
                sendActionbar(MessageManager.prefix + plugin.getAntiBotManager().replaceInfo(MessageManager.actionbarAntiBotMode));
            }else{
                sendActionbar(MessageManager.prefix + plugin.getAntiBotManager().replaceInfo(MessageManager.actionbarOffline));
            }
        }, false, 100L);
    }
}
