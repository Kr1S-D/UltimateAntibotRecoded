package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.server;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SatelliteServer {
    private final IAntiBotPlugin plugin;
    private final String sessionID;
    private long lastPingLatency;

    public SatelliteServer(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.lastPingLatency = 0;
        this.sessionID = UUID.randomUUID().toString();
        plugin.scheduleRepeatingTask(() -> {
            if(plugin.getAntiBotManager().isSomeModeOnline()) return;
            lastPingLatency = ping(sessionID);
        }, true, 1000L * 60L * 10L);
    }

    public long ping(String sessionID) {
        long a = System.currentTimeMillis();
        try {
            URL register = new URL(String.format("http://uabserver.kr1sd.me:8080/api/v1/handle?session=%s&platform=%s&onlinecount=%s&latency=%s", sessionID, PerformanceHelper.getRunning().toString(), plugin.getOnlineCount(), lastPingLatency));
            HttpURLConnection connection = (HttpURLConnection) register.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(2000);
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //plugin.getLogHelper().debug(reader.readLine());
            connection.disconnect();
        }catch (Exception e) {
            plugin.getLogHelper().debug("Error during contacting UAB servers, are they down?");
        }
        return System.currentTimeMillis() - a;
    }

    //public StatusResponseJSON getStatus(){
    //    try {
    //        URL url = new URL("http://satellite.kr1sd.me:8080/api/v1/status");
    //        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //        connection.setRequestMethod("GET");
    //        connection.setConnectTimeout(5000);
    //        connection.connect();
    //        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
    //        Gson gson = new Gson();
    //        StatusResponseJSON serverInfo = gson.fromJson(reader, StatusResponseJSON.class);
    //        reader.close();
    //        return serverInfo;
    //    }catch (Exception e){
    //        e.printStackTrace();
    //        plugin.getLogHelper().debug("Error during contacting UAB servers, are they down?");
    //        return null;
    //    }
    //}

    public long getLastPingLatency() {
        return lastPingLatency;
    }
}
