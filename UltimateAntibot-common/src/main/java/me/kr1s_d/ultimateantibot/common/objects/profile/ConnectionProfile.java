package me.kr1s_d.ultimateantibot.common.objects.profile;

import me.kr1s_d.ultimateantibot.common.server.packet.SatellitePacket;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ConnectionProfile implements Serializable, SatellitePacket {
    private String ip;
    private String nickname;
    private long firstJoin;
    private long lastJoin;

    private long minutePlayed;

    public ConnectionProfile(String ip, String nickname) {
        this.ip = ip;
        this.nickname = nickname;
        this.lastJoin = System.currentTimeMillis();
        this.firstJoin = System.currentTimeMillis();
        this.minutePlayed = 0;
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

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
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
            out.writeLong(firstJoin);
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
                ", firstJoin=" + firstJoin +
                ", lastJoin=" + lastJoin +
                ", minutePlayed=" + minutePlayed +
                '}';
    }
}
