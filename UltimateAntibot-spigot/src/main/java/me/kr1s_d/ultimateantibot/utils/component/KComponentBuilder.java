package me.kr1s_d.ultimateantibot.utils.component;

import me.kr1s_d.ultimateantibot.utils.component.impl.InteractableComponentBuilder;

public class KComponentBuilder {

    private KComponentBuilder() {
    }

    public static InteractableComponentBuilder interact() {
        return new InteractableComponentBuilder();
    }
}
