package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.ipapi.IPAPIProvider;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.proxycheck.ProxyCheckProvider;
import me.kr1s_d.ultimateantibot.common.objects.connectioncheck.proxycheck.result.ProxyResults;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.*;

public class VPNService implements IService {

    private final IAntiBotPlugin plugin;
    private final Map<String, ProxyResults> checkedResults;
    private final LogHelper logHelper;
    private final List<String> underVerification;

    public VPNService(IAntiBotPlugin plugin){
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
            if(ConfigManger.isIPApiVerificationEnabled){
                new IPAPIProvider(plugin).process(ip, name);
                underVerification.remove(ip);
            }
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
            new ProxyCheckProvider(plugin).process(ip, name);
        }, true,  0L);
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
