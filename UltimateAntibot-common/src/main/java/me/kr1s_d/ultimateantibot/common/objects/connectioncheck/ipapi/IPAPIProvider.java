package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.ipapi;

import com.google.gson.Gson;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.VPNProvider;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.tasks.TimedWhitelistTask;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class IPAPIProvider implements VPNProvider {
    private final IAntiBotPlugin plugin;

    public IPAPIProvider(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getID() {
        return "IP-APIProvider";
    }

    @Override
    public void process(String ip, String name) {
        VPNJSONResponse response = getResults(ip);
        if(response == null) return;

        if (response.isProxy()) {
            BlackListProfile profile = plugin.getAntiBotManager().getBlackListService().blacklistAndGet(ip, BlackListReason.VPN, name);
            plugin.disconnect(ip, MessageManager.getBlacklistedMessage(profile));
        } else {
            plugin.getAntiBotManager().getWhitelistService().whitelist(ip);
            plugin.scheduleDelayedTask(new TimedWhitelistTask(plugin, ip));
        }

    }

    @Override
    public String getCountry(String ip, String name) {
        VPNJSONResponse res = getResults(ip);
        return res == null ? "" : res.getCountryCode();
    }

    private VPNJSONResponse getResults(String ip){
        try {
            URL url = new URL(String.format("http://ip-api.com/json/%s?fields=country,proxy", ip.replace("/", "")));
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Gson gson = new Gson();
            VPNJSONResponse response = gson.fromJson(reader, VPNJSONResponse.class);
            reader.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class VPNJSONResponse {
        private boolean proxy;
        private String countryCode;

        public String getCountryCode() {
            return countryCode;
        }

        public boolean isProxy() {
            return proxy;
        }
    }
}
