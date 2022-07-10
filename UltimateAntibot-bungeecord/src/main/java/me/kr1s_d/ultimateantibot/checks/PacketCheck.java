package me.kr1s_d.ultimateantibot.checks;

import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
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
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    public void onUnLogin(String ip){
        joined.remove(ip);
        packetReceived.remove(ip);
        suspected.remove(ip);
    }

    public void registerJoin(String ip) {
        if (ConfigManger.getPacketCheckConfig().isEnabled() && !whitelistService.isWhitelisted(ip)) {
            joined.add(ip);
            removeTask(ip);
            checkForAttack();
        }
    }


    public void registerPacket(String ip){
        if(ConfigManger.getPacketCheckConfig().isEnabled() && joined.contains(ip) & !whitelistService.isWhitelisted(ip)) {
            packetReceived.add(ip);
        }
    }

    public void removeTask(String ip){
        iAntiBotPlugin.scheduleDelayedTask(() -> {
            joined.remove(ip);
            packetReceived.remove(ip);
        }, false, 1000L * ConfigManger.getPacketCheckConfig().getTime());
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
            if(suspected.size() >= ConfigManger.getPacketCheckConfig().getTrigger()){
                iAntiBotPlugin.getLogHelper().debug("Packet Check Executed!");
                Utils.disconnectAll(new ArrayList<>(suspected), MessageManager.getSafeModeMessage());
                for(String ip : suspected){
                    if(ConfigManger.getPacketCheckConfig().isBlacklist()) {
                        blacklist.blacklist(ip, BlackListReason.STRANGE_PLAYER);
                    }
                }
                if(ConfigManger.getPacketCheckConfig().isEnableAntiBotMode()) {
                    antibotManager.enableSlowAntiBotMode();
                }
                suspected.clear();
            }
        }, false, 1000L);
    }

    private boolean isEnabled(){
        return ConfigManger.getPacketCheckConfig().isEnabled();
    }

    private void loadTask(){
        if(!ConfigManger.getPacketCheckConfig().isEnabled()) return;
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
