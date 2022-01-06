package me.kr1s_d.ultimateantibot.checks;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PacketCheck {
    private final IAntiBotPlugin iAntiBotPlugin;
    private final Set<String> joined;
    private final Set<String> packetReceived;
    private final Set<String> suspected;
    private final IAntiBotManager antibotManager;
    private final BlackListService blacklist;
    private final WhitelistService whitelistService;

    public PacketCheck(IAntiBotPlugin plugin){
        this.iAntiBotPlugin = plugin;
        this.joined = new HashSet<>();
        this.packetReceived = new HashSet<>();
        this.suspected = new HashSet<>();
        this.antibotManager = plugin.getAntiBotManager();
        this.blacklist = plugin.getAntiBotManager().getBlackListService();
        this.whitelistService = plugin.getAntiBotManager().getWhitelistService();
        loadTask();
    }

    public void onUnLogin(String ip){
        joined.remove(ip);
        packetReceived.remove(ip);
        suspected.remove(ip);
    }

    public void registerJoin(String ip) {
        if (ConfigManger.getPacketCheckConfiguration().isEnabled() && !whitelistService.isWhitelisted(ip)) {
            joined.add(ip);
            removeTask(ip);
            checkForAttack();
        }
    }


    public void registerPacket(String ip){
        if(ConfigManger.getPacketCheckConfiguration().isEnabled() && joined.contains(ip) & !whitelistService.isWhitelisted(ip)) {
            packetReceived.add(ip);
        }
    }

    public void removeTask(String ip){
        iAntiBotPlugin.scheduleDelayedTask(() -> {
            joined.remove(ip);
            packetReceived.remove(ip);
        }, false, 1000L * ConfigManger.getPacketCheckConfiguration().getTime());
    }

    /**
     *
     */
    public void checkForAttack(){
        iAntiBotPlugin.scheduleDelayedTask(() -> {
            joined.forEach(user -> {
                if(!packetReceived.contains(user)){
                    suspected.add(user);
                }
            });
            if(suspected.size() >= ConfigManger.getPacketCheckConfiguration().getTrigger()){
                Utils.disconnectAll(new ArrayList<>(suspected), MessageManager.getSafeModeMessage());
                for(String ip : suspected){
                    if(ConfigManger.getPacketCheckConfiguration().isBlacklist()) {
                        blacklist.blacklist(ip, MessageManager.reasonStrangePlayer);
                    }
                }
                if(ConfigManger.getPacketCheckConfiguration().isEnableAntiBotMode()) {
                    antibotManager.enableSlowAntiBotMode();
                }
                suspected.clear();
            }
        }, false, 1000L);
    }

    private void loadTask(){
        if(!ConfigManger.getPacketCheckConfiguration().isEnabled()) return;
        iAntiBotPlugin.getLogHelper().debug("PacketCheck has been initialized!");
        iAntiBotPlugin.scheduleRepeatingTask(() -> {
            if(!antibotManager.isAntiBotModeEnabled()){
                return;
            }
            joined.clear();
            packetReceived.clear();
            suspected.clear();
        }, false, 1000L * ConfigManger.taskManagerClearPacket);
    }
}
