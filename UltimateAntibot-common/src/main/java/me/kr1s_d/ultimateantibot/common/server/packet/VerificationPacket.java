package me.kr1s_d.ultimateantibot.common.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VerificationPacket implements SatellitePacket {
    private final String ip;
    private final String nickname;
    private boolean verified;

    public VerificationPacket(String ip, String nickname) {
        this.ip = ip;
        this.nickname = nickname;
        this.verified = false;
    }

    public VerificationPacket(DataInputStream in)  {
        try {
            this.ip = in.readUTF();
            this.nickname = in.readUTF();
            this.verified = in.readBoolean();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to decode malformed packet " + this.getClass().getSimpleName());
        }
    }

    public String getIP() {
        return ip;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String getID() {
        return "0x03";
    }

    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeUTF(ip);
            out.writeUTF(nickname);
            out.writeBoolean(verified);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
