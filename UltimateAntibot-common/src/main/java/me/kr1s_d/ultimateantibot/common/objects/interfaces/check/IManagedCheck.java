package me.kr1s_d.ultimateantibot.common.objects.interfaces.check;

import me.kr1s_d.ultimateantibot.common.objects.enums.CheckListenedEvent;
import me.kr1s_d.ultimateantibot.common.objects.enums.CheckPriority;

public abstract class IManagedCheck implements IBasicCheck {

    public abstract CheckPriority getCheckPriority();

    public abstract CheckListenedEvent getListenedEvent();

}
