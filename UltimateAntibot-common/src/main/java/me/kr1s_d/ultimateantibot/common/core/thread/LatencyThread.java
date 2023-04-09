package me.kr1s_d.ultimateantibot.common.core.thread;

import me.kr1s_d.ultimateantibot.common.helper.PerformanceHelper;
import me.kr1s_d.ultimateantibot.common.helper.ServerType;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class LatencyThread {
    private String result;
    private double count;

    public LatencyThread(IAntiBotPlugin iAntiBotPlugin){
        iAntiBotPlugin.getLogHelper().debug("Enabled " + this.getClass().getSimpleName() + "!");
        this.result = "§cdisabled (see config)";
        this.count = 0;
        long nsv = 1250000000;
        if(PerformanceHelper.getRunning().equals(ServerType.SPIGOT)) {
            this.result = "§cdisabled (on bukkit)";
            return;
        }
        if(PerformanceHelper.isEnabled() && !PerformanceHelper.getPerformanceMode().isLatencyThreadEnabled()){
            iAntiBotPlugin.getLogHelper().warn("Since you have the performance mode enabled, we found that the latency thread must be disabled, otherwise it could create stability problems (read config), to enable it, you must disable it in the config section 'detect-server-performance', and activate the 'enable-latency-thread'");
            this.result = "§c(disabled (detect-server-performance: true))";
            return;
        }
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
                    this.result = String.valueOf(count);
                }
            }, "UAB#LatencyService").start();
        }
    }

    public String getLatency() {
        return result + " ms";
    }

}

