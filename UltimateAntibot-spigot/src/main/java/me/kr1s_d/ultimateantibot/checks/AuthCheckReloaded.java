package me.kr1s_d.ultimateantibot.checks;

import me.kr1s_d.ultimateantibot.UltimateAntiBotSpigot;
import me.kr1s_d.ultimateantibot.common.helper.enums.AuthCheckType;
import me.kr1s_d.ultimateantibot.common.helper.enums.ColorHelper;
import me.kr1s_d.ultimateantibot.common.objects.base.IncreaseInteger;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.ConnectionCheckerService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class AuthCheckReloaded {

    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final Map<String, AuthCheckType> checking;
    private final Map<String, AuthCheckType> completedCheck;
    private final Map<String, IncreaseInteger> pingMap;
    private final Map<String, Integer> pingData;
    private final Map<String, IncreaseInteger> failure;
    private final BukkitScheduler taskScheduler;
    private final Map<String, BukkitTask> runningTasks;
    private final ConnectionCheckerService connectionCheckerService;

    public AuthCheckReloaded(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.checking = new HashMap<>();
        this.completedCheck = new HashMap<>();
        this.pingMap = new HashMap<>();
        this.pingData = new HashMap<>();
        this.failure = new HashMap<>();
        this.taskScheduler = Bukkit.getScheduler();
        this.runningTasks = new HashMap<>();
        this.connectionCheckerService = plugin.getConnectionCheckerService();
        plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
    }


    public void onPing(ServerListPingEvent e, String ip){
        //Se sta eseguendo il ping check allora registra il ping
        if(isCompletingPingCheck(ip)){
            registerPing(ip);
            //Durante L'antibotMode:
            if(antiBotManager.isAntiBotModeEnabled()){
                int currentIPPings = pingMap.get(ip).get();
                int pingRequired = pingData.get(ip);
                //Impostazion e Dell'interfaccia per il conteggio dei ping
                if(currentIPPings == pingRequired){
                    e.setMotd(ColorHelper.colorize(MessageManager.verifiedPingInterface));
                    //ping.getVersion().setName(ColorHelper.colorize(MessageManager.verifiedPingInterface));
                }else{
                    e.setMotd(ColorHelper.colorize(MessageManager.normalPingInterface
                            .replace("$1", String.valueOf(currentIPPings))
                            .replace("$2", String.valueOf(pingRequired))
                    ));
                }
            }
        }
        //se ha superato il numero massimo di ping allora lo aggiunge nei fails
        if(hasExceededPingLimit(ip)){
            increaseFails(ip);
            resetData(ip);
        }
    }

    public void onJoin(AsyncPlayerPreLoginEvent e, String ip) {
        if (isCompletingPingCheck(ip)) {
            int currentIPPings = pingMap.getOrDefault(ip, new IncreaseInteger(0)).get();
            int pingRequired = pingData.getOrDefault(ip, 0);
            if (currentIPPings == pingRequired) {
                //checking connection
                if(ConfigManger.getProxyCheckConfig().isCheckFastJoin() && ConfigManger.getProxyCheckConfig().isEnabled()) {
                    connectionCheckerService.submit(ip, e.getName());
                }
                addToPingCheckCompleted(ip);
                checking.remove(ip);
            }
        }
        if(isWaitingResponse(ip)){
            resetTotal(ip);
            return;
        }
        int checkTimer = ThreadLocalRandom.current().nextInt(ConfigManger.authMinMaxTimer[0], ConfigManger.authMinMaxTimer[1]);
        if (hasCompletedPingCheck(ip)) {
            submitTimerTask(ip, checkTimer);
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ColorHelper.colorize(MessageManager.getTimerMessage(String.valueOf(checkTimer + 1))));
            return;
        }
        int pingTimer = ThreadLocalRandom.current().nextInt(ConfigManger.authMinMaxPing[0], ConfigManger.authMinMaxPing[1]);
        addToCompletingPingCheck(ip, pingTimer);
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ColorHelper.colorize(MessageManager.getPingMessage(String.valueOf(pingTimer))));
    }

    /**
     * Ritorna se l' IP può iniziare una nuova timer task
     * @param ip IP to check
     * @return Ritorna se l'IP può iniziare una nuova task
     */
    private boolean hasActiveTimerVerification(String ip){
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
        BukkitTask task = taskScheduler.runTaskLater(
                UltimateAntiBotSpigot.getInstance(),
                () -> {
                    addToWaiting(ip);
                    runningTasks.remove(ip);
                },
                Utils.convertToTicks(1000L * timer)
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
     * @param ip IP da resettare completamente
     */
    private void resetTotal(String ip){
        pingMap.remove(ip);
        checking.remove(ip);
        completedCheck.remove(ip);
        runningTasks.remove(ip);
        pingData.remove(ip);
        failure.remove(ip);
    }

    /**
     * @param ip IP da aggiungere
     * @param generatedPingAmount Numero di volte che deve pingare il server per eseguire un controllo corretto
     */
    private void addToCompletingPingCheck(String ip, int generatedPingAmount){
        pingMap.put(ip, new IncreaseInteger(0));
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
            pingMap.put(ip, new IncreaseInteger(0));
        }
    }

    /**
     * Aumenta il numero di volte che un ip ha fallito
     * il check
     * @param ip IP a cui si deve aumentare i fails
     */
    public void increaseFails(String ip){
        failure.getOrDefault(ip, new IncreaseInteger(0)).increase();
    }

}
