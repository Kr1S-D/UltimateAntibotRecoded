package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import java.math.BigDecimal;

public class LatencyThread {
    private double count;

    public LatencyThread(IAntiBotPlugin iAntiBotPlugin){
        iAntiBotPlugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.count = 0;
        long nanoSecondValue = 1250000000;
        new Thread(() -> {

            while (iAntiBotPlugin.isRunning()) {
                long first = System.nanoTime();
                try {
                    Thread.sleep(1250L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.count = System.nanoTime() - first - nanoSecondValue;
            }
        }, "UAB#LatencyService").start();
    }


    public String getLatency() {
        return String.valueOf(divide(count, 1E5));
    }

    public double getLatencyAsDouble(){
        return divide(count, 1E5);
    }

    public double divide(double f1, double f2) {
        BigDecimal a1 = BigDecimal.valueOf(f1);
        BigDecimal a2 = BigDecimal.valueOf(f2);
        return a1.divide(a2).doubleValue();
    }

}

