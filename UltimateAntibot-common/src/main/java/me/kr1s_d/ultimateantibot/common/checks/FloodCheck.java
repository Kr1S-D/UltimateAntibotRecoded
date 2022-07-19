package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.UABRunnable;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.Map;

public class FloodCheck implements ICheck {
    private final IAntiBotPlugin plugin;
    private final Map<String, Long> latencyMap;

    public FloodCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.latencyMap = new HashMap<>();
        loadTask();
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()) return false;

        long lastJoinMillis = latencyMap.getOrDefault(ip, (long) -1);
        if(lastJoinMillis == -1){
            latencyMap.put(ip, System.currentTimeMillis());
            return false;
        }
        long pastedMillis = System.currentTimeMillis() - lastJoinMillis;
        latencyMap.put(ip, System.currentTimeMillis());
        return ConfigManger.floodLatency >= pastedMillis;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isFloodCheckEnabled;
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(new UABRunnable() {
            @Override
            public boolean isAsync() {
                return true;
            }

            @Override
            public long getPeriod() {
                return 1000L * ConfigManger.floodTime;
            }

            @Override
            public void run() {
                latencyMap.clear();
            }
        });
    }
}
