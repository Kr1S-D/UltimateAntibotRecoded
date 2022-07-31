package me.kr1s_d.ultimateantibot.checks;

import me.kr1s_d.ultimateantibot.UltimateAntiBotBungeeCord;
import me.kr1s_d.ultimateantibot.common.AuthCheckType;
import me.kr1s_d.ultimateantibot.common.helper.ColorHelper;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.FancyInteger;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.ComponentBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AuthCheckReloaded {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antibotManager;
    private final Map<String, AuthCheckType> checking;
    private final Map<String, AuthCheckType> completedCheck;
    private final Map<String, FancyInteger> pingMap;
    private final Map<String, Integer> pingData;
    private final Map<String, FancyInteger> failure;
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledTask> runningTasks;
    private final Map<String, String> checkInitiator;
    private final VPNService VPNService;

    public AuthCheckReloaded(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antibotManager = plugin.getAntiBotManager();
        this.checking = new HashMap<>();
        this.completedCheck = new HashMap<>();
        this.pingMap = new HashMap<>();
        this.pingData = new HashMap<>();
        this.failure = new HashMap<>();
        this.taskScheduler = ProxyServer.getInstance().getScheduler();
        this.runningTasks = new HashMap<>();
        this.checkInitiator = new HashMap<>();
        this.VPNService = plugin.getVPNService();
        plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
    }


    public void onPing(ProxyPingEvent e, String ip){
        //Se sta eseguendo il ping check allora registra il ping
        if(isCompletingPingCheck(ip)){
            registerPing(ip);
            //Durante L'antibotMode:
            if(antibotManager.isAntiBotModeEnabled()){
                int currentIPPings = pingMap.get(ip).get();
                int pingRequired = pingData.get(ip);
                ServerPing ping = e.getResponse();
                ping.getVersion().setProtocol(0);
                //Impostazion e Dell'interfaccia per il conteggio dei ping
                if(currentIPPings == pingRequired){
                    ping.getVersion().setName(ColorHelper.colorize(MessageManager.verifiedPingInterface));
                }else{
                    ping.getVersion().setName(ColorHelper.colorize(MessageManager.normalPingInterface
                            .replace("$1", String.valueOf(currentIPPings))
                            .replace("$2", String.valueOf(pingRequired))
                    ));
                }
                //Imposto la risposta al ping
                e.setResponse(ping);
            }
        }
        //se ha superato il numero massimo di ping allora lo aggiunge nei fails
        if(hasExceededPingLimit(ip)){
            increaseFails(ip, e.getConnection().getName());
            resetData(ip);
        }
    }

    public void onJoin(PreLoginEvent e, String ip) {
        if (isCompletingPingCheck(ip)) {
            int currentIPPings = pingMap.get(ip).get();
            int pingRequired = pingData.getOrDefault(ip, 0);
            if (pingRequired != 0 && currentIPPings == pingRequired) {
                //0#133
                String initiator = checkInitiator.getOrDefault(ip, null);
                if(initiator.equals(e.getConnection().getName())) {
                    //checking connection
                    if (ConfigManger.getProxyCheckConfig().isCheckFastJoin() && !hasFailedThisCheck(ip, 2)) {
                        VPNService.submitIP(ip, e.getConnection().getName());
                    }
                    addToPingCheckCompleted(ip);
                    checking.remove(ip);
                }
                else{
                    resetData(ip);
                    increaseFails(ip, e.getConnection().getName());
                }
            }

            //se i ping sono inferiori quando entra ha fallito
            if(pingRequired != 0 && currentIPPings < pingRequired){
                increaseFails(ip, e.getConnection().getName());
            }
        }
        if(isWaitingResponse(ip)){
            resetTotal(ip);
            return;
        }

        int checkTimer = ThreadLocalRandom.current().nextInt(ConfigManger.authMinMaxTimer[0], ConfigManger.authMinMaxTimer[1]);
        if (hasCompletedPingCheck(ip)) {
            submitTimerTask(ip, checkTimer);
            e.setCancelReason(ComponentBuilder.buildColorized(MessageManager.getTimerMessage(String.valueOf(checkTimer + 1))));
            e.setCancelled(true);
            return;
        }
        int pingTimer = ThreadLocalRandom.current().nextInt(ConfigManger.authMinMaxPing[0], ConfigManger.authMinMaxPing[1]);
        //aggiungiamo l'ip nella verifica del ping timer
        addToCompletingPingCheck(ip, pingTimer);
        //registriamo ip e nome di chi inizia per controllare che siano gli stessi alla fine
        checkInitiator.put(ip, e.getConnection().getName());
        increaseFails(ip, e.getConnection().getName());
        e.setCancelReason(ComponentBuilder.buildColorized(
                MessageManager.getPingMessage(String.valueOf(pingTimer)))
        );
        e.setCancelled(true);
    }

    /**
     * Ritorna se l' IP può iniziare una nuova timer task
     * @param ip IP to check
     * @return Ritorna se l'IP può iniziare una nuova task
     */
    private boolean canStartTimerVerification(String ip){
        return runningTasks.containsKey(ip);
    }

    /**
     * Aggiunge una task allo scheduler con le informazioni del
     * player e del tempo che gli server aspettare per completarla
     *
     * @param ip IP da mettere in coda
     * @param timer tempo in coda
     */
    private void submitTimerTask(String ip, int timer){
        if(runningTasks.containsKey(ip)){
            runningTasks.get(ip).cancel();
        }
        ScheduledTask task = taskScheduler.schedule(
                UltimateAntiBotBungeeCord.getInstance(),
                () -> {
                    addToWaiting(ip);
                    runningTasks.remove(ip);
                },
                1000L * timer,
                TimeUnit.MILLISECONDS
        );
        runningTasks.put(ip, task);
    }

    /**
     * Ritorna se l'ip ha superato i ping richiesti dal check
     * @param ip IP da controllare
     * @return Se ha superato il numero di ping
     */
    private boolean hasExceededPingLimit(String ip){
        if(pingData.get(ip) == null || pingMap.get(ip) == null){
            return true;
        }
        return pingMap.get(ip).get() > pingData.get(ip);
    }

    /**
     * @param ip IP da resettare tranne i fail
     */
    private void resetData(String ip){
        pingMap.remove(ip);
        checking.remove(ip);
        completedCheck.remove(ip);
        if(runningTasks.containsKey(ip)){
            runningTasks.get(ip).cancel();
        }
        runningTasks.remove(ip);
        pingData.remove(ip);
        //failure.remove(ip);
    }

    /**
     *
     * @param ip The ip to check
     * @param min The min amount of times required to fail this check
     * @return if the ip has failed at least x min times this check
     */
    private boolean hasFailedThisCheck(String ip, int min){
        return failure.getOrDefault(ip, new FancyInteger(0)).get() >= min;
    }

    /**
     * @param ip IP da resettare completamente
     */
    private void resetTotal(String ip){
        pingMap.remove(ip);
        checking.remove(ip);
        completedCheck.remove(ip);
        runningTasks.remove(ip);
        pingData.remove(ip);
        failure.remove(ip);
        checkInitiator.remove(ip);
    }

    /**
     * @param ip IP da aggiungere
     * @param generatedPingAmount Numero di volte che deve pingare il server per eseguire un controllo corretto
     */
    private void addToCompletingPingCheck(String ip, int generatedPingAmount){
        pingMap.put(ip, new FancyInteger(0));
        pingData.put(ip, generatedPingAmount);
        checking.put(ip, AuthCheckType.PING);
    }

    /**
     * @param ip IP da controllare
     * @return Ritorna se ha completato il check del ping e può procedere con il resto dei controlli
     */
    private boolean hasCompletedPingCheck(String ip){
        return completedCheck.get(ip) != null && completedCheck.get(ip).equals(AuthCheckType.PING);
    }

    /**
     *
     * @param ip IP da controllare
     * @return Ritorna se il player sta eseguendo il ping check
     */
    private boolean isCompletingPingCheck(String ip){
        return checking.get(ip) != null && checking.get(ip).equals(AuthCheckType.PING);
    }

    /**
     * @param ip IP da controllare
     * @return Ritorna se il player è stato confermato e può entrare
     */
    private boolean isWaitingResponse(String ip){
        return completedCheck.get(ip) != null && completedCheck.get(ip).equals(AuthCheckType.WAITING);
    }

    /**
     * Aggiunge l'ip a coloro che quando si connetteranno
     * inizierà il countdown per il join/blacklist
     * @param ip IP da aggiungere
     */
    private void addToPingCheckCompleted(String ip){
        completedCheck.put(ip, AuthCheckType.PING);
    }

    /**
     * Aggiunge l'ip a coloro che quando si connettono
     * entreranno automaticamente nel server
     * @param ip IP da aggiungere
     */
    private void addToWaiting(String ip){
        completedCheck.put(ip, AuthCheckType.WAITING);
        plugin.scheduleDelayedTask(() -> {
            completedCheck.remove(ip);
            resetData(ip);
        }, false, ConfigManger.authBetween);
    }

    /**
     * Registra il ping al refresh
     * Aumentando i valori nella pingmap
     * @param ip IP da controllare
     */
    private void registerPing(String ip){
        if(pingMap.containsKey(ip)){
            pingMap.get(ip).increase();
        }else{
            pingMap.put(ip, new FancyInteger(0));
        }
    }

    /**
     * Aumenta il numero di volte che un ip ha fallito
     * il check
     * @param ip IP a cui si deve aumentare i fails
     */
    public void increaseFails(String ip, String name){
        FancyInteger current = failure.getOrDefault(ip, new FancyInteger(0));
        current.increase();
        failure.put(ip, current);

        if(current.get() >= ConfigManger.authMaxFails) {
            antibotManager.getBlackListService().blacklist(ip, BlackListReason.CHECK_FAILS, name);
            resetTotal(ip);
        }
    }

}
