package me.kr1s_d.ultimateantibot.common.objects.profile.entry;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MessageEntry {
    private String message;
    private long timeStamp;

    public MessageEntry(String message, long timeStamp) {
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getIP() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getSecondsTimeStamp() {
        if(timeStamp == -1) throw new UnsupportedOperationException("Wrong usage of MessageEntry#comparable");
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeStamp);
    }

    public static MessageEntry comparable(String name) {
        return new MessageEntry(name, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEntry that = (MessageEntry) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
