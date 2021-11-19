package me.kr1s_d.ultimateantibot.task;

import me.kr1s_d.ultimateantibot.AntBotManager;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;

public class ModeDisableTask implements Runnable {

    private final AntBotManager antBotManager;
    private final ModeType disableMode;

    public ModeDisableTask(AntBotManager antBotManager, ModeType disableMode) {
        this.antBotManager = antBotManager;
        this.disableMode = disableMode;
    }

    @Override
    public void run() {
        antBotManager.disableMode(disableMode);
    }
}
