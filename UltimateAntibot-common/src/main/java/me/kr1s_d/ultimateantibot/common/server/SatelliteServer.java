package me.kr1s_d.ultimateantibot.common.server;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.server.listener.SatelliteListener;
import me.kr1s_d.ultimateantibot.common.server.packet.SatellitePacket;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Deprecated
public class SatelliteServer {
    private final IAntiBotPlugin plugin;
    private final SatelliteListener listener;

    public SatelliteServer(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.listener = new SatelliteListener(plugin);

        plugin.runTask(listener);
        init();
    }

    private void init() {
        if(!listener.isConnected()) return;

        plugin.scheduleRepeatingTask(() -> {
            UserDataService users = plugin.getUserDataService();
            List<ConnectionProfile> profiles = users.getConnectionProfiles();

            for (ConnectionProfile profile : profiles) {
                profile.setMinutePlayed(0);
            }

            listener.sendPackets(profiles);
        }, false, 1000L * 60L);
    }

    public SatelliteListener getListener() {
        return listener;
    }
}
