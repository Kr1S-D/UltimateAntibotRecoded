package me.kr1s_d.ultimateantibot.common.checks;

public abstract class IManagedCheck implements ICheck {

    public abstract String getCheckName();

    public abstract double getCheckVersion();

    public abstract CheckPriority getCheckPriority();

    public abstract CheckListenedEvent getCheckListenedEvent();

    public abstract void onCancel(String ip, String name);

    public abstract boolean requireAntiBotMode();
}
