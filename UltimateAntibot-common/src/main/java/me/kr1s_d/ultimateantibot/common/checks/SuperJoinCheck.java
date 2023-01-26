package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.FancyInteger;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SuperJoinCheck implements JoinCheck {

    private final IAntiBotPlugin plugin;
    private final BlackListService blackListService;
    private final Map<String, FancyInteger> data;

    public SuperJoinCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.blackListService = plugin.getAntiBotManager().getBlackListService();
        this.data = new ConcurrentHashMap<>();
        loadTask();
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if (!isEnabled()) {
            return false;
        }
        FancyInteger i = data.getOrDefault(ip, new FancyInteger(0));
        i.increase();
        data.put(ip, i);

        if (i.get() > ConfigManger.superJoinLimit) {
            data.remove(ip);
            plugin.getLogHelper().debug("[UAB DEBUG] Detected attack on SuperJoinCheck!");
            return true;
        }

        return false;
    }

    @Override
    public void onDisconnect(String ip, String name) {

    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isSuperJoinEnabled;
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.superJoinTime);
    }
}
