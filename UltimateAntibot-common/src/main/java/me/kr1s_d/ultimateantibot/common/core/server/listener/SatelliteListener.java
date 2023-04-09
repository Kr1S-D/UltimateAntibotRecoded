package me.kr1s_d.ultimateantibot.common.core.server.listener;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.UABRunnable;
import me.kr1s_d.ultimateantibot.common.core.server.packet.AlertMessagePacket;
import me.kr1s_d.ultimateantibot.common.core.server.packet.SatellitePacket;
import me.kr1s_d.ultimateantibot.common.core.server.packet.VerificationPacket;
import me.kr1s_d.ultimateantibot.common.core.tasks.TimedWhitelistTask;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class SatelliteListener extends UABRunnable {
    private final IAntiBotPlugin plugin;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private boolean enabled = true;

    public SatelliteListener(IAntiBotPlugin plugin) {
        this.plugin = plugin;

        try {
            this.socket = new Socket(":D", 24324);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public long getPeriod() {
        return 0; //ignored here
    }

    @Override
    public void run() {
        while (enabled) {
            try {
                //ric
                try {
                    this.in = new DataInputStream(socket.getInputStream());
                }catch (Exception e) {

                }

                String id = in.readUTF();
                if (id.contains("x")) {
                    switch (id) {
                        case "0x02":
                            AlertMessagePacket alertMessagePacket = new AlertMessagePacket(in);
                            ServerUtil.broadcast(alertMessagePacket.getMessage());
                            break;
                        case "0x03":
                            VerificationPacket verPacket = new VerificationPacket(in);
                            if (verPacket.isVerified()) new TimedWhitelistTask(plugin, verPacket.getIP(), 20);
                            break;
                    }
                }

            } catch (IOException e) {
                if (!enabled) return;
                plugin.getLogHelper().info("The connection to the UAB servers has been lost, new connection attempt in 5 seconds!");
                enabled = false;

                if (plugin.isRunning()) {
                    plugin.scheduleDelayedTask(() -> {
                        enabled = true;
                        try {
                            this.socket = new Socket("uab-s1.kr1sd.me", 24324);
                            this.in = new DataInputStream(socket.getInputStream());
                            this.out = new DataOutputStream(socket.getOutputStream());
                            plugin.getLogHelper().info("The connection with the UAB servers has been successfully reestablished!");
                            run();
                        } catch (IOException e1) {
                            enabled = false;
                            e1.printStackTrace();
                            plugin.getLogHelper().info("The connection with the UAB servers has been lost, it is recommended to restart the server to restore it!");
                        }
                    }, true, 1000L * 5);
                }

                try {
                    socket.close();
                } catch (IOException ex) {
                    plugin.getLogHelper().warn("[SATELLITE] error during socket closing!");
                    ex.printStackTrace();
                }
            }
        }
    }

    public void closeChannel() {
        try {
            enabled = false;
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(SatellitePacket packet) {
        plugin.runTask(() -> sendPacketSync(packet), false);
    }

    public void sendPackets(SatellitePacket... packets) {
        plugin.runTask(() -> {
            for (SatellitePacket packet : packets) {
                try {
                    sendPacketSync(packet);
                } catch (Exception ignored) {
                }
            }
        }, true);
    }

    public <T extends SatellitePacket> void sendPackets(List<T> packets) {
        plugin.runTask(() -> {
            for (SatellitePacket packet : packets) {
                try {
                    sendPacketSync(packet);
                } catch (Exception ignored) {
                }
            }
        }, true);
    }


    private void sendPacketSync(SatellitePacket packet) {
        if (!enabled) return;
        if (out == null) return;
        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(packet.getID());
            packet.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return enabled;
    }
}
