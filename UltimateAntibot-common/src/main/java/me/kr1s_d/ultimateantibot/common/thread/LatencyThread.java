package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.enums.PerformanceMode;
import me.kr1s_d.ultimateantibot.common.helper.enums.ServerType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.Version;

import java.math.BigDecimal;

public class LatencyThread {
    private String result;
    private double count;

    public LatencyThread(IAntiBotPlugin iAntiBotPlugin){
        iAntiBotPlugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.result = "§cdisabled (see config)";
        this.count = 0;
        long nsv = 1250000000;
        if(PerformanceHelper.getRunning().equals(ServerType.SPIGOT)) {
            this.result = "§cdisabled on bukkit";
            return;
        }
        if(PerformanceHelper.isEnabled() && !PerformanceHelper.getPerformanceMode().isLatencyThreadEnabled()){
            iAntiBotPlugin.getLogHelper().warn("Since you have the performance mode enabled, we found that the latency thread must be disabled, otherwise it could create stability problems (read config), to enable it, you must disable it in the config section 'detect-server-performance', and activate the 'enable-latency-thread'");
            this.result = "§c(disabled (detect-server-performance: true))";
            return;
        }
        if(ConfigManger.enableLatencyThread) {
            iAntiBotPlugin.getLogHelper().info("PORCODIOOOOOOOOOOOOOOOOOO");
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
        return count + " ms";
    }

    public void divide(double f1, double f2) {
        BigDecimal a1 = BigDecimal.valueOf(f1);
        BigDecimal a2 = BigDecimal.valueOf(f2);
        result = String.valueOf(a1.divide(a2).doubleValue());
    }

}

