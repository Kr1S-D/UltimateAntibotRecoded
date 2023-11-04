package me.kr1s_d.ultimateantibot.common.objects.filter;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.FilterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ProxyAttackFilter implements Filter {
    private final IAntiBotManager antiBotManager;
    private final List<String> blocked;

    private final Proxy247Filter proxy247Filter;

    public ProxyAttackFilter(IAntiBotPlugin antiBotPlugin) {
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.proxy247Filter = new Proxy247Filter(antiBotPlugin);
        this.blocked = new ArrayList<>();
        FilterUtils.populateDefaultFilter(blocked);
        blocked.addAll(antiBotPlugin.getConfigYml().getStringList("attack-filter"));
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if (antiBotManager.isSomeModeOnline()) {
            if(antiBotManager.isPacketModeEnabled()) antiBotManager.increasePacketPerSecond();
            return !record.getMessage().toLowerCase().contains("{0}");
        }

        if(isDenied(record.getMessage())){
            antiBotManager.increasePacketPerSecond();
            if(antiBotManager.getPacketPerSecond() > ConfigManger.packetModeTrigger) antiBotManager.enablePacketMode();
        }

        return proxy247Filter.isLoggable(record);
    }


    public boolean isDenied(String record) {
        for (String str : blocked) {
            if (record.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
