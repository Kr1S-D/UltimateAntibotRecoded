package me.kr1s_d.ultimateantibot.common.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AlertMessagePacket implements SatellitePacket {
    private String message;

    public AlertMessagePacket() {

    }

    public AlertMessagePacket(String message) {
        this.message = message;
    }

    public AlertMessagePacket(DataInputStream in) {
        try {
            this.message = in.readUTF();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to decode malformed packet " + this.getClass().getSimpleName());
        }
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getID() {
        return "0x02";
    }

    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
