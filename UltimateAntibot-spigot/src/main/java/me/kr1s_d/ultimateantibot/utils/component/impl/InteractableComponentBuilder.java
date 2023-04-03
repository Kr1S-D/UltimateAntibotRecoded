package me.kr1s_d.ultimateantibot.utils.component.impl;

import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class InteractableComponentBuilder {
    private final TextComponent component = new TextComponent();

    public InteractableComponentBuilder text(String text) {
        component.setText(ServerUtil.colorize(text));
        return this;
    }

    public InteractableComponentBuilder click(ClickEvent.Action action, String value) {
        component.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public InteractableComponentBuilder hover(HoverEvent.Action action, String value) {
        component.setHoverEvent(new HoverEvent(action, new ArrayList<>(Collections.singletonList(new Text(value)))));
        return this;
    }

    public void send(Player player) {
        player.spigot().sendMessage(component);
    }
}
