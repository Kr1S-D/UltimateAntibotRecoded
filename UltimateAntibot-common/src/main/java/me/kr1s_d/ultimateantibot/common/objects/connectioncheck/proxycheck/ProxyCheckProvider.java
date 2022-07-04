package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.proxycheck;

import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.base.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.VPNProvider;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.proxycheck.result.ProxyResults;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

public class ProxyCheckProvider implements VPNProvider {
    private final IAntiBotPlugin plugin;

    public ProxyCheckProvider(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getID() {
        return "ProxyCheck";
    }

    @Override
    public void process(String ip, String name) {
        ConnectionCheck connectionCheck = new ConnectionCheck();
        ProxyResults results = connectionCheck.getAndMapResults(ip.replace("/", ""));
        if(results == null){
            plugin.getLogHelper().warn("Your API key has reached the daily limit or is not valid!");
            return;
        }
        plugin.getLogHelper().debug("ConnectionCheckerService --> checked " + ip + " result " + results.getProxy());
        if(results.getProxy().equals("yes")){
            BlackListProfile profile = plugin.getAntiBotManager().getBlackListService().blacklistAndGet(ip, BlackListReason.VPN, name);
            plugin.disconnect(ip, MessageManager.getBlacklistedMessage(profile));
        }else{
            if(results.getProxy().equals("no")){
                plugin.getAntiBotManager().getWhitelistService().whitelist(ip);
            }
        }
    }
}
