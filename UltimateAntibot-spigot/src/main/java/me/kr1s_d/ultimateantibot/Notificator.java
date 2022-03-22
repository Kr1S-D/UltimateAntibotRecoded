package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.INotificator;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Notificator implements INotificator {
    private static final List<Player> actionbars = new ArrayList<>();
    private static final List<Player> titles = new ArrayList<>();
    //private static final BossBar bar = Bukkit.createBossBar("&fWaiting for a new attack!", BarColor.RED, BarStyle.SOLID);

    public static void automaticNotification(Player player) {
    //    if (actionbars.contains(player)) {
    //        return;
    //    }
    //    if (bar.getPlayers().contains(player)) {
     //       return;
    //    }
     //   if (ConfigManger.enableBossBarAutomaticNotification) {
     //       bar.addPlayer(player);
     //   }
    //    actionbars.add(player);
    }

    public static void toggleBossBar(Player player){
    //    if(bar.getPlayers().contains(player)){
    //        bar.removePlayer(player);
    //    }else{
    //        bar.addPlayer(player);
    //     }
    //    player.sendMessage((ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledBossBar)));
    }

    public static void toggleActionBar(Player player){
        if(actionbars.contains(player)){
            actionbars.remove(player);
        }else {
            actionbars.add(player);
        }
        player.sendMessage(ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledActionbar));
    }

    public static void toggleTitle(Player player){
        if(titles.contains(player)){
            titles.remove(player);
        }else {
            titles.add(player);
        }
        player.sendMessage(ColorHelper.colorize(MessageManager.prefix + MessageManager.toggledTitle));
    }


    public void sendActionbar(String str){
        actionbars.forEach(ac -> Utils.sendActionbar(ac, ColorHelper.colorize(str)));
    }

    public void sendTitle(String title, String subtitle){
        titles.forEach(t -> t.sendTitle(
                UltimateAntiBotSpigot.getInstance().getAntiBotManager().replaceInfo(ColorHelper.colorize(title)),
                UltimateAntiBotSpigot.getInstance().getAntiBotManager().replaceInfo(ColorHelper.colorize(subtitle)),
                0,
                30,
                0
        ));
    }

    @Override
    public void sendBossBarMessage(String str, float health) {
        //bar.setTitle(ColorHelper.colorize(str));
        //bar.setProgress(health);
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
