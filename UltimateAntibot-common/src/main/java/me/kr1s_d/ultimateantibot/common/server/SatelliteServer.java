package me.kr1s_d.ultimateantibot.common.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.server.listener.SatelliteListener;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class SatelliteServer {
    private final IAntiBotPlugin plugin;
    private SatelliteListener listener;

    private final String sessionID;
    private long lastPingLatency;

    public SatelliteServer(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        //this.listener = new SatelliteListener(plugin);

        //plugin.runTask(listener);
        //init();

        this.sessionID = UUID.randomUUID().toString();
        plugin.scheduleRepeatingTask(() -> {
            if (plugin.getAntiBotManager().isSomeModeOnline()) {
                return;
            }

            this.lastPingLatency = ping(this.sessionID);
        }, true, 600000L);
    }

    /**
     * No more used
     */
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

    /**
     * @return deprecated
     */
    public SatelliteListener getListener() {
        return listener;
    }

    public long ping(String sessionID) {
        long a = System.currentTimeMillis();
        try {
            URL register = new URL(String.format("http://uabserver.kr1sd.me:8080/api/v1/handle?session=%s&platform=%s&onlinecount=%s", sessionID, PerformanceHelper.getRunning().toString(), this.plugin.getOnlineCount()));
            HttpURLConnection connection = (HttpURLConnection)register.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(2000);
            connection.connect();

            // Lettura della risposta dalla connessione come una stringa JSON
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Deserializzazione dell'array JSON di indirizzi IP in una lista di stringhe
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> ipList = gson.fromJson(response.toString(), listType);

            // whitelist inside server framework
            for (String ip : ipList) {
                plugin.getAntiBotManager().getWhitelistService().whitelist(ip, 10);
            }
            connection.disconnect();
        } catch (Exception e) {
            //this.plugin.getLogHelper().debug("Error during contacting UAB servers, are they down?");
        }
        return System.currentTimeMillis() - a;
    }
}
