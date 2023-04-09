package me.kr1s_d.ultimateantibot.common.core.thread;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.INotificator;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

public class AttackAnalyzerThread {

    private final IAntiBotManager antiBotManager;
    private final INotificator notificator;
    private long lastAnalyzedBot;
    private long connections;


    public AttackAnalyzerThread(IAntiBotPlugin plugin) {
        plugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        lastAnalyzedBot = 1;
        connections = 0;
        this.notificator = plugin.getNotificator();
        this.antiBotManager = plugin.getAntiBotManager();
        plugin.scheduleRepeatingTask(() -> {
            connections = antiBotManager.getConnectionPerSecond();
            if (antiBotManager.isSomeModeOnline()) {
                executeAlert();
                lastAnalyzedBot = connections;
            }else{
               notificator.sendBossBarMessage(MessageManager.bossBarIdleMessage, 0);
            }
        }, false, 1000L);
    }


    private void executeAlert() {
        double botpercent = calculatePercentage(connections, lastAnalyzedBot);
        float bt = (float) (botpercent / 100);
        if(bt >= 1){
            bt = 1;
        }
        if (connections > lastAnalyzedBot) {
            notificator.sendBossBarMessage(MessageManager.attackAnalyzerIncrease
                    .replace("%perc%", String.valueOf(botpercent))
                    .replace("%old%", String.valueOf(lastAnalyzedBot))
                    .replace("%new%", String.valueOf(connections))
            , bt);
            //increase
        } else {
            if(connections < lastAnalyzedBot){
                notificator.sendBossBarMessage(MessageManager.attackAnalyzerDecrease
                        .replace("%perc%", String.valueOf(botpercent))
                        .replace("%old%", String.valueOf(lastAnalyzedBot))
                        .replace("%new%", String.valueOf(connections))
                , bt);
            }
            //decrease
        }
    }


    private double calculatePercentage(double prima, double dopo){
        if(prima == dopo){
            return 0;
        }
        if(dopo == 0){
            dopo = 1;
        }
        if(prima == 0){
            prima = 1;
        }
        double d1 = dopo - prima;
        double las = Math.round((d1 / prima) * 100L);
        if(las < 0){
            las *= -1;
        }
        return las;
    }
}
