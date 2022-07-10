package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.common.helper.ColorHelper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private Utils(){}

    public static String getIP(ProxiedPlayer player){
        return player.getAddress().getAddress().toString();
    }

    public static String getIP(PendingConnection connection){
        return connection.getAddress().getAddress().toString();
    }

    public static String getIP(Connection connection){
        return connection.getAddress().getAddress().toString();
    }

    public static String convertToString(List<String> stringList) {
        return String.join(System.lineSeparator(), stringList);
    }

    public static void disconnectPlayerFromIp(String ip, List<String> reason){
        for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
            if(getIP(p).equalsIgnoreCase(ip)){
                p.disconnect(new TextComponent(ColorHelper.colorize(convertToString(reason))));
            }
        }
    }

    public static void disconnectPlayerFromIp(String ip,String reason){
        for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
            if(getIP(p).equalsIgnoreCase(ip)){
                p.disconnect(new TextComponent(reason));
            }
        }
    }

    public static void disconnectAll(List<String> ips, List<String> reason){
        ProxyServer.getInstance().getPlayers().forEach(player -> {
            ips.forEach(ip -> {
                if(Utils.getIP(player).equals(ip)){
                    player.disconnect(new TextComponent(ColorHelper.colorize(convertToString(reason))));
                }
            });
        });
    }

    public static void disconnectAll(List<String> ips, String reason){
        ProxyServer.getInstance().getPlayers().forEach(player -> {
            ips.forEach(ip -> {
                if(Utils.getIP(player).equals(ip)){
                    player.disconnect(new TextComponent(ColorHelper.colorize(reason)));
                }
            });
        });
    }

    public static void debug(String str){
        for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
            p.sendMessage(str);
        }
    }

    public static String colora(String str){
        return ColorHelper.colorize(str);
    }

    public static List<String> coloraLista(List<String> str){
        List<String> a = new ArrayList<>();
        str.forEach(b -> a.add(colora(b)));
        return a;
    }

    public static List<String> calculatePlayerNames(){
        List<String> a = new ArrayList<>();
        ProxyServer.getInstance().getPlayers().forEach(b -> a.add(b.getName()));
        return a;
    }
}
