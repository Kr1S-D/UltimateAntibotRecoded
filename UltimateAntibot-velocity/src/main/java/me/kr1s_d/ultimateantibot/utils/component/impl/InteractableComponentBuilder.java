package me.kr1s_d.ultimateantibot.utils.component.impl;

import com.velocitypowered.api.proxy.Player;
import me.kr1s_d.ultimateantibot.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class InteractableComponentBuilder {
    private TextComponent component = null;

    public InteractableComponentBuilder(String text) {
        this.component = ColorUtils.format(text, Component::text);
    }

    public InteractableComponentBuilder click(ClickEvent.Action action, String value) {
        component.clickEvent(ClickEvent.clickEvent(action, value));
        return this;
    }

    public InteractableComponentBuilder hover(HoverEvent.Action action, String value) {
        component.hoverEvent(HoverEvent.hoverEvent(action, value));
        return this;
    }

    public void send(Player player) {
        player.sendMessage(component);
    }

    public TextComponent getComponent() {
        return component;
    }
}
