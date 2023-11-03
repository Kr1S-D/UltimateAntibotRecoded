package me.kr1s_d.ultimateantibot.checks;

import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;

import java.util.*;

public class PacketCheckVelocity {
    private final IAntiBotPlugin iAntiBotPlugin;
    private final Set<String> joined;
    private final Set<String> packetReceived;
    private final Set<String> suspected;
    private final IAntiBotManager antibotManager;
    private final BlackListService blacklist;
    private final WhitelistService whitelistService;

    public PacketCheckVelocity(IAntiBotPlugin plugin) {
        this.iAntiBotPlugin = plugin;
        this.joined = new HashSet<>();
        this.packetReceived = new HashSet<>();
        this.suspected = new HashSet<>();
        this.antibotManager = plugin.getAntiBotManager();
        this.blacklist = plugin.getAntiBotManager().getBlackListService();
        this.whitelistService = plugin.getAntiBotManager().getWhitelistService();

        List<String> invalidPlugins = Arrays.asList("Geyser-BungeeCord", "Protocolize");

        for (String invalidPlugin : invalidPlugins) {
            if (UltimateAntiBotVelocity.getInstance().getServer().getPluginManager().getPlugin(invalidPlugin).orElse(null) != null) {
                iAntiBotPlugin.getLogHelper().warn("The packet check has been automatically disabled to prevent false positives given by the presence of the plugin " + invalidPlugin + " (which could alter its correct functioning)");
                ConfigManger.getPacketCheckConfig().setEnabled(false);
            }
        }

        if (isEnabled()) {
            loadTask();
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    public void onUnLogin(String ip) {
        joined.remove(ip);
        packetReceived.remove(ip);
        suspected.remove(ip);
    }

    public void registerJoin(String ip) {
        if (isEnabled() && !whitelistService.isWhitelisted(ip)) {
            joined.add(ip);
            checkForAttack();
        }
    }

    public void registerPacket(String ip) {
        if (isEnabled() && !whitelistService.isWhitelisted(ip)) {
            packetReceived.add(ip);
        }
    }

    public void checkForAttack() {
        iAntiBotPlugin.scheduleDelayedTask(() -> {
            joined.forEach(user -> {
                if (!packetReceived.contains(user)) {
                    suspected.add(user);
                }
            });

            suspected.removeIf(packetReceived::contains);

            if (suspected.size() >= ConfigManger.getPacketCheckConfig().getTrigger()) {
                iAntiBotPlugin.getLogHelper().debug("Packet Check Executed!");
                Utils.disconnectAll(new ArrayList<>(suspected), MessageManager.getSafeModeMessage());
                for (String ip : new ArrayList<>(suspected)) {
                    if (ConfigManger.getPacketCheckConfig().isBlacklist()) {
                        blacklist.blacklist(ip, BlackListReason.STRANGE_PLAYER);
                    }
                }
                if (ConfigManger.getPacketCheckConfig().isEnableAntiBotMode()) {
                    antibotManager.enableSlowAntiBotMode();
                }
                suspected.clear();
                iAntiBotPlugin.getLogHelper().debug("[UAB DEBUG] Detected attack on PacketCheck!");
            }
        }, false, 2500L);
    }

    private boolean isEnabled() {
        return ConfigManger.getPacketCheckConfig().isEnabled();
    }

    private void loadTask() {
        iAntiBotPlugin.scheduleRepeatingTask(() -> {
            if (!antibotManager.isAntiBotModeEnabled()) {
                return;
            }
            joined.clear();
            packetReceived.clear();
            suspected.clear();
        }, false, 1000L * ConfigManger.taskManagerClearPacket);
    }
}
