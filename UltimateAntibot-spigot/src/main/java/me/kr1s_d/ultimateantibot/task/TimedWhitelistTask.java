package me.kr1s_d.ultimateantibot.task;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;

public class TimedWhitelistTask implements Runnable{

    private final IAntiBotManager antiBotManager;
    private final String ip;

    public TimedWhitelistTask(IAntiBotManager antiBotManager, String ip) {
        this.antiBotManager = antiBotManager;
        this.ip = ip;
    }

    @Override
    public void run() {
        antiBotManager.getWhitelistService().unWhitelist(ip);
    }
}
