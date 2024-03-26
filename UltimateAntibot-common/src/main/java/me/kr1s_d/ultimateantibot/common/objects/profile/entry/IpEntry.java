package me.kr1s_d.ultimateantibot.common.objects.profile.entry;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IpEntry implements Serializable {
    private String ip;
    private long lastJoin;

    public IpEntry(String ip, long lastJoin) {
        this.ip = ip;
        this.lastJoin = lastJoin;
    }

    public String getIP() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public long getSecondsFromLastJoin() {
        if(lastJoin == -1) throw new UnsupportedOperationException("Wrong usage of IpEntry#comparable");
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastJoin);
    }

    public static IpEntry comparable(String name) {
        return new IpEntry(name, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpEntry that = (IpEntry) o;
        return Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }
}
