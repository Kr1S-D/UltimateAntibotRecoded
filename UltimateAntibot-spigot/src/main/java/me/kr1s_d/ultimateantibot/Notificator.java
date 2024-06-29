package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.INotificator;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import me.kr1s_d.ultimateantibot.utils.KBossBar;
import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.Version;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Notificator implements INotificator {
    private static final List<Player> actionbars = new ArrayList<>();
    private static final List<Player> titles = new ArrayList<>();
    private static final KBossBar bar = new KBossBar();

    public static void automaticNotification(Player player) {
        if(actionbars.contains(player)) return;
        actionbars.remove(player);
        bar.removePlayer(player);
        if (ConfigManger.enableBossBarAutomaticNotification && Version.getBukkitServerVersion() > 19) {
            bar.addPlayer(player);
        }
        actionbars.add(player);
    }

    public static void toggleBossBar(Player player){
        if(!bar.isSupported()) return;
        if(Version.getBukkitServerVersion() < 19) {
            player.sendMessage(Utils.colora(MessageManager.prefix + "&cBossbar&f notifications are not supported on &c1.8.x!"));
            return;
        }
        if(bar.getPlayers().contains(player)){
            bar.removePlayer(player);
        }else{
            bar.addPlayer(player);
        }
        player.sendMessage((ServerUtil.colorize(MessageManager.prefix + MessageManager.toggledBossBar)));
    }

    public static void toggleActionBar(Player player){
        if(actionbars.contains(player)){
            actionbars.remove(player);
        }else {
            actionbars.add(player);
        }
        player.sendMessage(ServerUtil.colorize(MessageManager.prefix + MessageManager.toggledActionbar));
    }

    public static void toggleTitle(Player player){
        if(titles.contains(player)){
            titles.remove(player);
        }else {
            titles.add(player);
        }
        player.sendMessage(ServerUtil.colorize(MessageManager.prefix + MessageManager.toggledTitle));
    }

    public static void onQuit(Player player) {
        titles.remove(player);
        bar.removePlayer(player);
        actionbars.remove(player);
    }

    public static void disableAllNotifications() {
        actionbars.clear();
        bar.getPlayers().forEach(p -> bar.removePlayer(p.getPlayer()));
        titles.clear();
    }

    public void sendActionbar(String str){
        actionbars.forEach(ac -> Utils.sendActionbar(ac, ServerUtil.colorize(str)));
    }

    public void sendTitle(String title, String subtitle){
        titles.forEach(t -> t.sendTitle(
                UltimateAntiBotSpigot.getInstance().getAntiBotManager().replaceInfo(ServerUtil.colorize(title)),
                UltimateAntiBotSpigot.getInstance().getAntiBotManager().replaceInfo(ServerUtil.colorize(subtitle)),
                0,
                30,
                0
        ));
    }

    @Override
    public void sendBossBarMessage(String str, float health) {
        if(!bar.isSupported()) return;
        bar.setTitle(ServerUtil.colorize(str));
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
