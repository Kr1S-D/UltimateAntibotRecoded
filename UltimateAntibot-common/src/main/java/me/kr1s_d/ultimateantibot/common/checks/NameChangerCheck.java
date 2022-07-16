package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NameChangerCheck extends IManagedCheck {

    private final IAntiBotPlugin plugin;
    private final BlackListService blackListService;
    private final Map<String, Set<String>> data;

    public NameChangerCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.blackListService = plugin.getAntiBotManager().getBlackListService();
        this.data = new HashMap<>();
        loadTask();
        if (isEnabled()) {
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    /**
     * @param ip   ip of the player
     * @param name name of the player
     * @return if player neeed to be disconnected from the check
     */
    public boolean isDenied(String ip, String name) {
        if (!isEnabled()) {
            return false;
        }
        if (data.containsKey(ip)) {
            Set<String> nicks = data.get(ip);
            nicks.add(name);
            return nicks.size() >= ConfigManger.nameChangerLimit;
        } else {
            data.put(ip, new HashSet<>());
        }
        return false;
    }

    @Override
    public void onDisconnect(String ip, String name) {

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
    public boolean isEnabled() {
        return ConfigManger.isNameChangerEnabled;
    }

    @Override
    public void loadTask() {
        plugin.scheduleRepeatingTask(data::clear, false, 1000L * ConfigManger.nameChangerTime);
    }

    @Override
    public CheckPriority getCheckPriority() {
        return CheckPriority.HIGHEST;
    }

    @Override
    public CheckListenedEvent getCheckListenedEvent() {
        return CheckListenedEvent.PRELOGIN;
    }

    @Override
    public void onCancel(String ip, String name) {
        blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
    }

    @Override
    public boolean requireAntiBotMode() {
        return true;
    }
}
