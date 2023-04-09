package me.kr1s_d.ultimateantibot.common.objects.profile;

import me.kr1s_d.ultimateantibot.common.core.server.packet.SatellitePacket;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ConnectionProfile implements Serializable, SatellitePacket {
    private static final long serialVersionUID = 7293619371231515414L;
    private String ip;
    private String nickname;
    private long firstJoinDate;
    private long lastJoin;
    private long minutePlayed;

    private boolean firstJoin;

    public ConnectionProfile(String ip, String nickname) {
        this.ip = ip;
        this.nickname = nickname;
        this.lastJoin = System.currentTimeMillis();
        this.firstJoinDate = System.currentTimeMillis();
        this.minutePlayed = 0;
        this.firstJoin = true;
    }

    public void onJoin(String nickname) {
        this.nickname = nickname;
        this.lastJoin = System.currentTimeMillis();
    }

    public void onDisconnect() {
        minutePlayed += TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastJoin);
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getFirstJoinDate() {
        return firstJoinDate;
    }

    public void setFirstJoinDate(long firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
    }

    public long getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public long getMinutePlayed() {
        return minutePlayed;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    public void setMinutePlayed(long minutePlayed) {
        this.minutePlayed = minutePlayed;
    }

    public int getDaysFromLastJoin() {
        return (int) TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastJoin);
    }

    @Override
    public String getID() {
        return "0x00";
    }

    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeUTF(ip);
            out.writeUTF(nickname);
            out.writeLong(firstJoinDate);
            out.writeLong(lastJoin);
            out.writeLong(minutePlayed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ConnectionProfile{" +
                "ip='" + ip + '\'' +
                ", nickname='" + nickname + '\'' +
                ", firstJoin=" + firstJoinDate +
                ", lastJoin=" + lastJoin +
                ", minutePlayed=" + minutePlayed +
                '}';
    }
}
