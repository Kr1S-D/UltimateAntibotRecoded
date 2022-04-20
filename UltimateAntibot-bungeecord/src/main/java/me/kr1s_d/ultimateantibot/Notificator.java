package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.objects.enums.BarColor;
import me.kr1s_d.ultimateantibot.common.objects.enums.BarStyle;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.INotificator;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.objects.DynamicBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.packet.BossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Notificator implements INotificator {

    private static final List<ProxiedPlayer> actionbars = new ArrayList<>();
    private static final List<ProxiedPlayer> titles = new ArrayList<>();
    private static final DynamicBar bar = new DynamicBar("&fWaiting for a new attack!", BarColor.RED, BarStyle.SOLID);


    public static void automaticNotification(ProxiedPlayer player){
        if(actionbars.contains(player)) return;
        actionbars.remove(player);
        bar.removePlayer(player);
        if(player.getPendingConnection().getVersion() > 106){
            if(bar.hasPlayer(player)){
                return;
            }
            if(ConfigManger.enableBossBarAutomaticNotification){
                bar.addPlayer(player);
            }
        }
        actionbars.add(player);
    }

    public static void toggleBossBar(ProxiedPlayer player){
        if(bar.hasPlayer(player)){
            bar.removePlayer(player);
        }else{
            bar.addPlayer(player);
        }
        player.sendMessage(new TextComponent(ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledBossBar)));
    }

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


    public void sendActionbar(String str){
        actionbars.forEach(ac -> ac.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorHelper.colorize(str))));
    }

    public void sendTitle(String title, String subtitle){
        Title t = ProxyServer.getInstance().createTitle();
        t.title(new TextComponent(ColorHelper.colorize(UltimateAntiBotBungeeCord.getInstance().getAntiBotManager().replaceInfo(title))));
        t.subTitle(new TextComponent(ColorHelper.colorize(UltimateAntiBotBungeeCord.getInstance().getAntiBotManager().replaceInfo(subtitle))));
        t.stay(20);
        t.fadeIn(0);
        t.fadeOut(0);
        titles.forEach(t::send);
    }

    @Override
    public void sendBossBarMessage(String str, float health) {
        bar.setTitle(ColorHelper.colorize(str));
        bar.setProgress(health);
    }

    public void init(IAntiBotPlugin plugin){
        plugin.scheduleRepeatingTask(() -> {
            if(plugin.getAntiBotManager().isSomeModeOnline()){
                sendTitle(MessageManager.titleTitle, plugin.getAntiBotManager().replaceInfo(MessageManager.titleSubtitle));
            }
            if(plugin.getAntiBotManager().isPacketModeEnabled()){
                sendActionbar(plugin.getAntiBotManager().replaceInfo(MessageManager.actionbarPackets));
                return;
            }
            if(plugin.getAntiBotManager().isSomeModeOnline()) {
                sendActionbar(plugin.getAntiBotManager().replaceInfo(MessageManager.actionbarAntiBotMode));
            }else{
                sendActionbar(plugin.getAntiBotManager().replaceInfo(MessageManager.actionbarOffline));
            }
        }, false, 125L);
    }
}
