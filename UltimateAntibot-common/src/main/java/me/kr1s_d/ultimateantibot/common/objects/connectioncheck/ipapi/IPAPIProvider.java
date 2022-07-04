package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.ipapi;

import com.google.gson.Gson;
import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.base.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.VPNProvider;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
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
        try {
            URL url = new URL("https://api.minecraft-italia.it/v5/server-info/RubyCraft/?key=44d3e62af0289515961600d6c83e34a3");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Gson gson = new Gson();
            VPNJSONResponse response = gson.fromJson(reader, VPNJSONResponse.class);

            if(response.isProxy()){
                BlackListProfile profile = plugin.getAntiBotManager().getBlackListService().blacklistAndGet(ip, BlackListReason.VPN, name);
                plugin.disconnect(ip, MessageManager.getBlacklistedMessage(profile));
            }else{
                plugin.getAntiBotManager().getWhitelistService().whitelist(ip);
            }

            reader.close();
        } catch (IOException e) {

        }
    }


    private static class VPNJSONResponse {
        private boolean proxy;

        public boolean isProxy() {
            return proxy;
        }
    }
}
