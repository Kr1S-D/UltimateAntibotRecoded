package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.Map;

public class SuperJoinCheck extends IManagedCheck {

    private final IAntiBotPlugin plugin;
    private final BlackListService blackListService;
    private final Map<String, Integer> data;

    public SuperJoinCheck(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.blackListService = plugin.getAntiBotManager().getBlackListService();
        this.data = new HashMap<>();
        loadTask();
        if(isEnabled()){
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    @Override
    public boolean isDenied(String ip, String name) {
        if(!isEnabled()){
            return false;
        }
        if(data.containsKey(ip)){
            int times = data.get(ip);
            if(times > ConfigManger.superJoinLimit){
                return true;
            }
            data.put(ip, times + 1);
        }else{
            data.put(ip, 0);
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

    @Override
    public String getCheckName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public double getCheckVersion() {
        return 4.0;
    }

    @Override
    public CheckPriority getCheckPriority() {
        return CheckPriority.HIGH;
    }

    @Override
    public CheckListenedEvent getCheckListenedEvent() {
        return CheckListenedEvent.PRELOGIN;
    }

    @Override
    public void onCancel(String ip, String name) {
        blackListService.blacklist(ip, BlackListReason.TOO_MUCH_JOINS, name);
    }

    @Override
    public boolean requireAntiBotMode() {
        return true;
    }
}
