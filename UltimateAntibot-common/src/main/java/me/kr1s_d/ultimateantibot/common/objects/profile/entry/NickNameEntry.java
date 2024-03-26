package me.kr1s_d.ultimateantibot.common.objects.profile.entry;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NickNameEntry implements Serializable {
    private String currentName;
    private long lastJoin;

    public NickNameEntry(String currentName, long lastJoin) {
        this.currentName = currentName;
        this.lastJoin = lastJoin;
    }

    public String getName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public long getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public long getSecondsFromLastJoin() {
        if(lastJoin == -1) throw new UnsupportedOperationException("Wrong usage of NickNameEntry#comparable");
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastJoin);
    }

    public static NickNameEntry comparable(String name) {
        return new NickNameEntry(name, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NickNameEntry that = (NickNameEntry) o;
        return Objects.equals(currentName, that.currentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentName);
    }
}
