package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.helper.enums.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.base.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.ConnectionCheck;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.result.ProxyResults;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.*;

public class ConnectionCheckerService implements IService {

    private final IAntiBotPlugin plugin;
    private final Map<String, ProxyResults> checkedResults;
    private final LogHelper logHelper;
    private final List<String> underVerification;

    public ConnectionCheckerService(IAntiBotPlugin plugin){
        this.plugin = plugin;
        checkedResults = new HashMap<>();
        logHelper = plugin.getLogHelper();
        this.underVerification = new ArrayList<>();
    }

    @Override
    public void load() {
        logHelper.debug("Loaded " + this.getClass().getSimpleName() + "!");
    }

    @Override
    public void unload() {

    }

    public void submit(String ip, String name){
        if(checkedResults.get(ip) != null || underVerification.contains(ip)){
            return;
        }
        underVerification.add(ip);
        checkedResults.remove(ip);
        plugin.scheduleDelayedTask(() -> {
            if(!ConfigManger.getProxyCheckConfig().isEnabled()){
                underVerification.remove(ip);
                logHelper.debug("API key not set! - ProxyCheck is offline!");
                return;
            }
            if(underVerification.size() > 7){
                logHelper.debug("Too many verification requests! - Clearing...");
                underVerification.clear();
                return;
            }
            underVerification.remove(ip);
            ConnectionCheck connectionCheck = new ConnectionCheck();
            ProxyResults results = connectionCheck.getAndMapResults(ip.replace("/", ""));
            logHelper.debug("ConnectionCheckerService --> checked " + ip + " result " + results.getProxy());
            if(results.getProxy().equals("yes")){
                BlackListProfile profile = plugin.getAntiBotManager().getBlackListService().blacklistAndGet(ip, BlackListReason.VPN, name);
                plugin.disconnect(ip, MessageManager.getBlacklistedMessage(profile));
            }else{
                if(results.getProxy().equals("no")){
                    plugin.getAntiBotManager().getWhitelistService().whitelist(ip);
                }
            }
        }, true,  500L);
    }

    public boolean isReady(String ip){
        return checkedResults.get(ip) != null;
    }

    public ProxyResults getResults(String ip){
        return checkedResults.get(ip);
    }

    public int getUnderVerificationSize(){
        return underVerification.size();
    }

    public List<String> getIPSUnderVerification() {
        return underVerification;
    }
}
