package me.kr1s_d.ultimateantibot.utils.component;

import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import me.kr1s_d.ultimateantibot.utils.component.impl.InteractableComponentBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class KComponentBuilder {

    private KComponentBuilder() {}

    public static BaseComponent colorized(String str) {
        return new TextComponent(ServerUtil.colorize(str));
    }

    public static InteractableComponentBuilder interact() {
        return new InteractableComponentBuilder();
    }
}
