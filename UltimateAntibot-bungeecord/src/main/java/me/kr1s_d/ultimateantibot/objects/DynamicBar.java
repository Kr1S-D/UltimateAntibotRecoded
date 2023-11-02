package me.kr1s_d.ultimateantibot.objects;

import com.google.common.collect.Sets;
import me.kr1s_d.ultimateantibot.common.BarColor;
import me.kr1s_d.ultimateantibot.common.BarStyle;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicBar {
        private static final AtomicInteger barID = new AtomicInteger(1);

        private final UUID uuid;
        private String title;
        private float progress = 1;
        private BarColor barColor;
        private BarStyle barStyle;
        private String compiledTitle;
        private boolean visible = true;
        private boolean checkBrokenBar = false;

        private final Set<ProxiedPlayer> players = Sets.newHashSet();

        public DynamicBar(String title, BarColor barColor, BarStyle barStyle) {
            this.uuid = UUID.nameUUIDFromBytes(("BBB:" + barID.getAndIncrement()).getBytes(StandardCharsets.UTF_8));
            this.title = title;
            this.compiledTitle = title;
            this.barColor = barColor;
            this.barStyle = barStyle;
        }

        public String getTitle() {
            return title;
        }

        public void updateBossBar(String title) {
            if(checkBrokenBar) return;
            this.title = title;
            this.compiledTitle = title;

            if (visible) {
                net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(uuid, 3);
                updateBossBar(packet, new TextComponent(title));
                this.players.forEach(player -> player.unsafe().sendPacket(packet));
            }
        }

    private void updateBossBar(Object bossBar, BaseComponent title) {
        try {
            Class<?> bossBarClass = bossBar.getClass();

            // Try BaseComponentMethod
            try {
                Method baseComponent = bossBarClass.getDeclaredMethod("setTitle", BaseComponent.class);
                baseComponent.invoke(bossBar, new TextComponent(title));
            } catch (NoSuchMethodException ex) {
                // Try StringMethod
                try {
                    Method setTitleMethodWithString = bossBarClass.getDeclaredMethod("setTitle", String.class);
                    setTitleMethodWithString.invoke(bossBar, ComponentSerializer.toString(title));
                } catch (NoSuchMethodException stringMethodNotFound) {
                    // Nessuno dei due metodi è disponibile
                    ServerUtil.log(LogHelper.LogType.ERROR, "Unable to set bossbar title, missing method?");
                    ServerUtil.log(LogHelper.LogType.ERROR, "Disabling bossbar function...");
                    checkBrokenBar = true;
                    players.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public BarColor getBarColor() {
            return barColor;
        }

        public void setBarColor(BarColor barColor) {
            this.barColor = barColor;

            if (visible) {
                net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(uuid, 4);
                packet.setColor(this.barColor.ordinal());
                packet.setDivision(this.barStyle.ordinal());
                this.players.forEach(player -> player.unsafe().sendPacket(packet));
            }
        }

        public BarStyle getBarStyle() {
            return barStyle;
        }

        public void setBarStyle(BarStyle barStyle) {
            this.barStyle = barStyle;

            if (visible) {
                net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(uuid, 4);
                packet.setColor(this.barColor.ordinal());
                packet.setDivision(this.barStyle.ordinal());
                this.players.forEach(player -> player.unsafe().sendPacket(packet));
            }
        }

        public float getProgress() {
            return progress;
        }

        public void updateProgress(float progress) {
            if(checkBrokenBar) return;
            this.progress = progress;

            if (visible) {
                net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(uuid, 2);
                packet.setHealth(this.progress);
                this.players.forEach(player -> player.unsafe().sendPacket(packet));
            }
        }

        public boolean hasPlayer(ProxiedPlayer player) {
            return this.players.contains(player);
        }

        public void addPlayer(ProxiedPlayer player) {
            if(checkBrokenBar) return;
            this.players.add(player);

            if (visible && player.isConnected()) {
                player.unsafe().sendPacket(getAddPacket());
            }
        }

        public void removePlayer(ProxiedPlayer player) {
            if(checkBrokenBar) return;
            this.players.remove(player);

            if (visible && player.isConnected()) {
                player.unsafe().sendPacket(getRemovePacket());
            }
        }

        public void removeAll() {
            new HashSet<>(this.players).forEach(this::removePlayer);
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            if (visible == this.visible) {
                return;
            }

            this.visible = visible;

            net.md_5.bungee.protocol.packet.BossBar packet = visible ? getAddPacket() : getRemovePacket();
            this.players.forEach(player -> player.unsafe().sendPacket(packet));
        }

        private net.md_5.bungee.protocol.packet.BossBar getAddPacket() {
            net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(uuid, 0);
            updateBossBar(packet, new TextComponent(this.compiledTitle));
            packet.setColor(this.barColor.ordinal());
            packet.setDivision(this.barStyle.ordinal());
            packet.setHealth(this.progress);
            return packet;
        }

        private net.md_5.bungee.protocol.packet.BossBar getRemovePacket() {
            return new net.md_5.bungee.protocol.packet.BossBar(uuid, 1);
        }

}
