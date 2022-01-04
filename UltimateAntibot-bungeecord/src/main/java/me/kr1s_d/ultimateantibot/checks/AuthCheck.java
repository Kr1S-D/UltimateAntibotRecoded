package me.kr1s_d.ultimateantibot.checks;

import me.kr1s_d.ultimateantibot.UltimateAntiBotBungeeCord;
import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.tasks.TimedWhitelist;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.ComponentBuilder;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class AuthCheck {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antibotManager;
    private final List<String> pendingChecks;
    private final List<String> completedChecksWaiting;
    private final List<String> pingCheckCompleted;
    private final Map<String, Integer> pingMap;
    private final Map<String, Integer> requiredPing;

    public AuthCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antibotManager = plugin.getAntiBotManager();
        this.pendingChecks = new ArrayList<>();
        this.completedChecksWaiting = new ArrayList<>();
        this.pingCheckCompleted = new ArrayList<>();
        this.pingMap = new HashMap<>();
        this.requiredPing = new HashMap<>();
        loadTask();
    }

    /**
     * Controlla se pendingcheck non contiene
     * l'ip, in tal caso inizia la verifica
     */
    public void startCountDown(String ip, int waitTime){
        if(!pendingChecks.contains(ip)){
            pendingChecks.add(ip);
            plugin.scheduleDelayedTask(() -> {
                completedChecksWaiting.add(ip);
                betweenCountDown(ip);
            }, false, 1000L * waitTime);
        }
    }

    /**
     * controlla se l'ip è pronto per entrare
     */

    private boolean isWaitingResponse(String ip){
        return completedChecksWaiting.contains(ip);
    }

    /**
     * Controlla se l'ip è in attesa di verifica
     */

    private boolean isPending(String ip){
        return pendingChecks.contains(ip);
    }

    private void betweenCountDown(String ip){
        plugin.scheduleDelayedTask(() -> {
            completedChecksWaiting.remove(ip);
            pendingChecks.remove(ip);
        }, false, 1000L * ConfigManger.authBetween);
    }

    /**
     * Controlla se il ping check è stato eseguito
     */

    public boolean hasCompletedPingCheck(String ip){
        return pingCheckCompleted.contains(ip);
    }

    private void reset(String ip){
        pendingChecks.remove(ip);
        completedChecksWaiting.remove(ip);
        pingCheckCompleted.remove(ip);
        pingMap.remove(ip);
        requiredPing.remove(ip);
    }
    /**
     * Controlla se il player quando si connette ha
     * Esattamente quel numero di ping, in questo caso
     * lo aggiunge nella lista dei completati
     * altrimenti lo resetta
     */

    public void checkForJoin(PreLoginEvent e, String ip){
        if(isWaitingResponse(ip)){
            antibotManager.getWhitelistService().whitelist(ip);
            plugin.scheduleDelayedTask(new TimedWhitelist(antibotManager.getWhitelistService(), ip),false,500);
            reset(ip);
            return;
        }
        if(hasCompletedPingCheck(ip)){
            int check_timer = ThreadLocalRandom.current().nextInt(ConfigManger.authMinMaxTimer[0], ConfigManger.authMinMaxTimer[1]);
            if(!pingMap.get(ip).equals(requiredPing.get(ip))){
                reset(ip);
            }
            startCountDown(ip, check_timer);
            e.setCancelReason(ComponentBuilder.buildColorized(MessageManager.getTimerMessage(String.valueOf(check_timer + 1))));
            e.setCancelled(true);
        }else{
            int check_ping = ThreadLocalRandom.current().nextInt(ConfigManger.authMinMaxPing[0], ConfigManger.authMinMaxPing[1]);
            reset(ip);
            startPing(ip, check_ping);
            e.setCancelReason(ComponentBuilder.buildColorized(
                    MessageManager.getPingMessage(String.valueOf(check_ping)))
            );
            e.setCancelled(true);
        }
    }

    /**
     * Controlla se le mappe contengono i ping precisi del giocatore
     * quando si connette al server, in questo caso lo aggiunge alla
     * lista di coloro che hanno completato il check
     * altrimenti resetta le mappe per l'ip interesssato
     */

    private void startPing(String ip, int times){
        if(!ConfigManger.authEnabled) return;
        if(pingMap.containsKey(ip) && requiredPing.containsKey(ip) && !pingMap.get(ip).equals(requiredPing.get(ip))){
            reset(ip);
            return;
        }
        if(pingMap.containsKey(ip) && requiredPing.containsKey(ip) && pingMap.get(ip).equals(requiredPing.get(ip))){
            pingCheckCompleted.add(ip);
        }else{
            pingMap.put(ip, 0);
            requiredPing.put(ip, times);
            pingCheckCompleted.remove(ip);
        }
    }

    /**
     * Regista i ping dei giocatori
     */

    public void onPing(ProxyPingEvent e, String ip){
        if(pingMap.containsKey(ip)){
            pingMap.put(ip, pingMap.get(ip) + 1);
            if(antibotManager.isAntiBotModeEnabled()){
                ServerPing ping = e.getResponse();
                ping.getVersion().setProtocol(0);
                if(pingMap.get(ip).equals(requiredPing.get(ip))){
                    ping.getVersion().setName(ColorHelper.colorize(MessageManager.verifiedPingInterface));
                }else{
                    ping.getVersion().setName(ColorHelper.colorize(MessageManager.normalPingInterface.replace("$2", String.valueOf(requiredPing.get(ip))).replace("$1", String.valueOf(pingMap.get(ip)))));
                }
                e.setResponse(ping);
            }
            if(pingMap.get(ip).equals(requiredPing.get(ip))) {
                pingCheckCompleted.add(ip);
            }
        }
    }

    private void loadTask(){
        if(!ConfigManger.authEnabled) return;
        plugin.getLogHelper().debug("AuthCheck has been initialized!");
        plugin.scheduleRepeatingTask(() -> {
            if(!antibotManager.isAntiBotModeEnabled()){
                return;
            }
            pendingChecks.clear();
            completedChecksWaiting.clear();
            pingCheckCompleted.clear();
            pingMap.clear();
            requiredPing.clear();
        }, false, 1000L * ConfigManger.authResetTime);
    }
}
