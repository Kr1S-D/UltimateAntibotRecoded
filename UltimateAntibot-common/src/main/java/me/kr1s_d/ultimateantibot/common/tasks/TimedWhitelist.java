package me.kr1s_d.ultimateantibot.common.tasks;

import me.kr1s_d.ultimateantibot.common.service.WhitelistService;

public class TimedWhitelist implements Runnable{

    private final WhitelistService whitelistService;
    private final String ip;

    public TimedWhitelist(WhitelistService whitelistService, String ip){
        this.whitelistService = whitelistService;
        this.ip = ip;
    }

    @Override
    public void run() {
        whitelistService.unWhitelist(ip);
    }
}
