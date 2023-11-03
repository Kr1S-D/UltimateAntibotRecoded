package me.kr1s_d.ultimateantibot.utils.component;

import me.kr1s_d.ultimateantibot.utils.Utils;
import me.kr1s_d.ultimateantibot.utils.component.impl.InteractableComponentBuilder;
import net.kyori.adventure.text.Component;

public class KComponentBuilder {
    private KComponentBuilder() {

    }

    public static Component colorized(String str) {
        return Utils.colora(str);
    }

    public static InteractableComponentBuilder interact(String text) {
        return new InteractableComponentBuilder(text);
    }
}
