package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.UltimateAntiBotSpigot;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static long convertToTicks(long millis){
        // 1000 : 20 = millis : x
        return millis * 20L / 1000;
    }

    public static String getPlayerIP(Player player) {
        return "/" + player.getAddress().getAddress().getHostAddress();
    }

    public static String getInetSocketAddressIP(InetSocketAddress address){
        return "/" + address.getAddress().getHostAddress();
    }

    public static String getInetAddressIP(InetAddress address){
        return "/" + address.getHostAddress();
    }

    public static String colora(String str){
        return ServerUtil.colorize(str);
    }

    public static List<String> coloraLista(List<String> col){
        List<String> a = new ArrayList<>();
        for(String c : col){
            a.add(colora(c));
        }
        return a;
    }

    public static void disconnectAll(List<String> str, String reason){
        str.forEach(ip -> {
            UltimateAntiBotSpigot.getInstance().disconnect(ip, reason);
        });
    }

    public static void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;

        if (Version.getBukkitServerVersion() > 19) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colora(message)));
            return;
        }
        try {
            //in this case spigot jar will be legacy version so this method will 100% work
            String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
            nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1"))
                method = chat.getDeclaredMethod("a", String.class);
            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[] { String.class }).newInstance(message);
            Object packetPlayOutChat = ppoc.getConstructor(new Class[] { chatBaseComponent, byte.class }).newInstance(object, (byte) 2);
            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
