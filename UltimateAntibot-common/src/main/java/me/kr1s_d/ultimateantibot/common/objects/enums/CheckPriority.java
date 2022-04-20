package me.kr1s_d.ultimateantibot.common.objects.enums;

public enum CheckPriority {
    LOWEST(4),
    LOW(3),
    NORMAL(2),
    HIGH(1),
    HIGHEST(0);

    private final int priority;

    CheckPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
