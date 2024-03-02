package me.kr1s_d.ultimateantibot.utils;

import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import net.kyori.adventure.text.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils() {}

    public static String getIP(Player player) {
        return "/" + player.getRemoteAddress().getAddress().getHostAddress();
    }

    public static String getIP(InetSocketAddress address){
        return "/" + address.getAddress().getHostAddress();
    }

    public static String getIP(InetAddress address){
        return "/" + address.getHostAddress();
    }

    public static String convertToString(List<String> stringList) {
        return String.join(System.lineSeparator(), stringList);
    }

    public static void disconnectPlayerFromIp(String ip, List<String> reason){
        for(Player p : UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers()){
            if(getIP(p).equalsIgnoreCase(ip)){
                p.disconnect(Component.text(ServerUtil.colorize(convertToString(reason))));
            }
        }
    }

    public static void disconnectPlayerFromIp(String ip, String reason){
        for(Player p : UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers()){
            if(getIP(p).equalsIgnoreCase(ip)){
                p.disconnect(Component.text(reason));
            }
        }
    }

    public static void disconnectAll(List<String> ips, List<String> reason){
        UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers().forEach(player -> {
            ips.forEach(ip -> {
                if(Utils.getIP(player).equals(ip)){
                    player.disconnect(Component.text(ServerUtil.colorize(convertToString(reason))));
                }
            });
        });
    }

    public static void disconnectAll(List<String> ips, String reason){
        UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers().forEach(player -> {
            ips.forEach(ip -> {
                if(Utils.getIP(player).equals(ip)){
                    player.disconnect(Component.text(ServerUtil.colorize(reason)));
                }
            });
        });
    }

    public static void debug(String str){
        for(Player p : UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers()){
            p.sendMessage(Component.text().content(str));
        }
    }

    public static Component colora(String str) {
        return ColorUtils.format(str);
    }

    public static List<Component> coloraLista(List<String> str){
        List<Component> a = new ArrayList<>();
        str.forEach(b -> a.add(colora(b)));
        return a;
    }

    public static List<String> calculatePlayerNames() {
        List<String> a = new ArrayList<>();
        UltimateAntiBotVelocity.getInstance().getServer().getAllPlayers().forEach(b -> a.add(b.getGameProfile().getName()));
        return a;
    }
}
