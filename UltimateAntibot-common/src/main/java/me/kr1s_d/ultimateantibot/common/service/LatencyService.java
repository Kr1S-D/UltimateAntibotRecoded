package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import java.math.BigDecimal;

public class LatencyService {
    private double count;

    public LatencyService(IAntiBotPlugin iAntiBotPlugin){
        this.count = 0;
        long nanoSecondValue = 100000000;
        new Thread(() -> {
            while (iAntiBotPlugin.isRunning()) {
                long first = System.nanoTime();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.count = System.nanoTime() - first - nanoSecondValue;
            }
        }, "UAB#LatencyService").start();
    }


    public String getLatency() {
        return divide(count, 1E6);
    }

    public String divide(double f1, double f2) {
        BigDecimal a1 = BigDecimal.valueOf(f1);
        BigDecimal a2 = BigDecimal.valueOf(f2);
        String str = a1.divide(a2).toString();
        if (str.length() >= 4) {
            return str.substring(0, 4);
        } else {
            return "0";
        }
    }

}

