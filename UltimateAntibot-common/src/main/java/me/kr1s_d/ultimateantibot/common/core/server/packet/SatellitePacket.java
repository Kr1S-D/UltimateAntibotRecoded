package me.kr1s_d.ultimateantibot.common.core.server.packet;

import java.io.DataOutputStream;

public interface SatellitePacket {

    String getID();

    void write(DataOutputStream output);
}
