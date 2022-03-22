package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Updater {
    private final IAntiBotPlugin plugin;
    private final String localVersion;
    private String newVersion;

    public Updater(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.localVersion = plugin.getVersion();
        this.newVersion = "not_found";
        check();
        checkNotification();
    }

    private boolean isAvailable;

    public boolean isAvailable() {
        return isAvailable;
    }


    public void checkNotification() {
        if(isAvailable()) {
            plugin.scheduleRepeatingTask(() -> {
                LogHelper log = plugin.getLogHelper();
                log.warn("&fA new &cversion&f has been found!");
                log.warn("&fUpdate the &cplugin&f as soon as possible!");
                log.warn("&fCurrent version &c$1, &fNew version &c$2".replace("$1", localVersion).replace("$2", newVersion));
                log.warn("&fDownload new version here: &c&nultimateantibot.kr1sd.me");
            }, false, 60000 * 10L);
        }
    }

    public void check() {
        isAvailable = checkUpdate();
    }

    private boolean checkUpdate() {
        try {
            String id = "93439";
            String url = "https://api.spigotmc.org/legacy/update.php?resource=";
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url + id).openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            newVersion = raw;
            if(!localVersion.equalsIgnoreCase(raw))
                return true;

        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
