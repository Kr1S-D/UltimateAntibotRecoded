package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentBuilder {

    private ComponentBuilder() {}

    public static BaseComponent buildColorized(String str){
        return new TextComponent(ColorHelper.colorize(str));
    }
}
