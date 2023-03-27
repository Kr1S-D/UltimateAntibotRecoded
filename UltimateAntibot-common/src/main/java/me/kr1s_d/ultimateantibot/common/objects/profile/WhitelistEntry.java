package me.kr1s_d.ultimateantibot.common.objects.profile;

import java.util.concurrent.TimeUnit;

public class WhitelistEntry {
    private final String ip;
    private final long time;

    public WhitelistEntry(String ip, long minutes) {
        this.ip = ip;
        this.time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes);
    }

    public String getIp() {
        return ip;
    }

    public boolean canBeRemoved() {
        return System.currentTimeMillis() - time > 0;
    }
}
