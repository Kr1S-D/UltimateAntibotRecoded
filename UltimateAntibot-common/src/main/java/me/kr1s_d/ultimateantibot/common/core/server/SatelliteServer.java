package me.kr1s_d.ultimateantibot.common.core.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.core.server.listener.SatelliteListener;
import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.FileUtil;

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

    private String sessionID;
    private long lastPingLatency;

    public SatelliteServer(IAntiBotPlugin plugin) {
        this.plugin = plugin;

        String sessionID = FileUtil.readLine("sessionID.uab", FileUtil.UABFolder.DATA);

        try {
            this.sessionID = UUID.fromString(sessionID).toString();
        }catch (Exception e) {
            this.sessionID = UUID.randomUUID().toString();
            FileUtil.writeLine("sessionID.uab", FileUtil.UABFolder.DATA, this.sessionID);
        }

        plugin.scheduleRepeatingTask(() -> {
            if (plugin.getAntiBotManager().isSomeModeOnline()) {
                return;
            }

            this.lastPingLatency = ping(this.sessionID);
        }, true, 600000L);
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

            plugin.getLogHelper().debug("[SATELLITE] res: " + response.toString());
            JsonObject object = JsonParser.parseString(response.toString()).getAsJsonObject();
            CloudConfig.a = object.get("a").getAsBoolean();
            CloudConfig.i = object.get("b").getAsInt();
            object.get("c").getAsString();
            plugin.getLogHelper().debug("[SATELLITE] json: " + object.toString());

            connection.disconnect();
        } catch (Exception e) {
            //this.plugin.getLogHelper().debug("Error during contacting UAB servers, are they down?");
        }
        return System.currentTimeMillis() - a;
    }
}
