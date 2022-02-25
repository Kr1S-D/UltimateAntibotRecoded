package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.math.BigDecimal;

public class LatencyThread {
    private String result;
    private double count;

    public LatencyThread(IAntiBotPlugin iAntiBotPlugin){
        iAntiBotPlugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.result = "§cdisabled (see config)";
        this.count = 0;
        long nsv = 1250000000;
        if(ConfigManger.enableLatencyThread) {
            new Thread(() -> {
                while (iAntiBotPlugin.isRunning()) {
                    long first = System.nanoTime();
                    try {
                        Thread.sleep(1250L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.count = System.nanoTime() - first - nsv;
                }
            }, "UAB#LatencyService").start();
        }
    }

    public String getLatency() {
        return result;
    }

    public void divide(double f1, double f2) {
        BigDecimal a1 = BigDecimal.valueOf(f1);
        BigDecimal a2 = BigDecimal.valueOf(f2);
        result = String.valueOf(a1.divide(a2).doubleValue());
    }

}

