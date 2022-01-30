package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.INotificator;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

public class AttackAnalyzerThread {

    private final IAntiBotManager antiBotManager;
    private final INotificator notificator;
    private long lastAnalyzedBot;
    private long currentBots;


    public AttackAnalyzerThread(IAntiBotPlugin plugin) {
        plugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        lastAnalyzedBot = 1;
        currentBots = 0;
        this.notificator = plugin.getNotificator();
        this.antiBotManager = plugin.getAntiBotManager();
        new Thread(() -> {
            while (plugin.isRunning()) {
                currentBots = antiBotManager.getJoinPerSecond();
                if (antiBotManager.isSomeModeOnline()) {
                        executeAlert();
                        lastAnalyzedBot = currentBots;
                }else{
                    //BAR AFK DEFAULT MESSAGE
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "UAB#AttackAnalyzer").start();
    }


    private void executeAlert() {
        double botpercent = calculatePercentage(currentBots, lastAnalyzedBot);
        float bt = (float) (botpercent / 100);
        if(bt >= 1){
            bt = 1;
        }
        if (currentBots > lastAnalyzedBot) {
            notificator.sendBossBarMessage(MessageManager.attackAnalyzerIncrease
                    .replace("%perc%", String.valueOf(botpercent))
                    .replace("%old%", String.valueOf(lastAnalyzedBot))
                    .replace("%new%", String.valueOf(currentBots))
            , bt);
            //increase
        } else {
            if(currentBots < lastAnalyzedBot){
                notificator.sendBossBarMessage(MessageManager.attackAnalyzerDecrease
                        .replace("%perc%", String.valueOf(botpercent))
                        .replace("%old%", String.valueOf(lastAnalyzedBot))
                        .replace("%new%", String.valueOf(currentBots))
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
