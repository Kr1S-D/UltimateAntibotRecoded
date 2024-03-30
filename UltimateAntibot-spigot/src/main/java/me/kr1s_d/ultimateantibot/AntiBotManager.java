package me.kr1s_d.ultimateantibot;

import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;
import me.kr1s_d.ultimateantibot.common.cache.JoinCache;
import me.kr1s_d.ultimateantibot.common.core.AttackWatcher;
import me.kr1s_d.ultimateantibot.common.core.detectors.AttackDurationDetector;
import me.kr1s_d.ultimateantibot.common.core.thread.DynamicCounterThread;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.VPNService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.Formatter;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import me.kr1s_d.ultimateantibot.event.AttackStateEvent;
import me.kr1s_d.ultimateantibot.event.ModeEnableEvent;
import me.kr1s_d.ultimateantibot.utils.EventCaller;

public class AntiBotManager implements IAntiBotManager {
    private final IAntiBotPlugin iAntiBotPlugin;
    private final DynamicCounterThread joinPerSecond;
    private final DynamicCounterThread pingPerSecond;
    private final DynamicCounterThread packetPerSecond;
    private final DynamicCounterThread connectionPerSecond;
    private final AttackDurationDetector attackDurationDetector;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    private final QueueService queueService;
    private ModeType modeType;
    private boolean isAntiBotModeOnline;
    private boolean isSlowAntiBotModeOnline;
    private boolean isPacketModeEnabled;
    private boolean isPingModeEnabled;
    private final LogHelper logHelper;
    private final JoinCache joinCache;
    private final VPNService VPNService;
    private final AttackWatcher attackWatcher;

    public AntiBotManager(IAntiBotPlugin plugin) {
        this.iAntiBotPlugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.joinPerSecond = new DynamicCounterThread(plugin);
        this.pingPerSecond = new DynamicCounterThread(plugin);
        this.packetPerSecond = new DynamicCounterThread(plugin);
        this.connectionPerSecond = new DynamicCounterThread(plugin);
        this.attackDurationDetector = new AttackDurationDetector(plugin);
        this.queueService = new QueueService();
        this.blackListService = new BlackListService(plugin, queueService, plugin.getBlackList(), logHelper);
        this.whitelistService = new WhitelistService(queueService, plugin.getWhitelist(), logHelper);
        this.modeType = ModeType.OFFLINE;
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.joinCache = new JoinCache();
        this.VPNService = plugin.getVPNService();
        this.attackWatcher = new AttackWatcher(this);
    }

    @Override
    public long getJoinPerSecond() {
        return joinPerSecond.getSlowCount();
    }

    @Override
    public long getSpeedJoinPerSecond() {
        return joinPerSecond.getSpeedCount();
    }

    @Override
    public long getPingPerSecond() {
        return pingPerSecond.getSlowCount();
    }

    @Override
    public long getPacketPerSecond() {
        return packetPerSecond.getSlowCount();
    }

    @Override
    public long getConnectionPerSecond() {
        return connectionPerSecond.getSlowCount();
    }

    @Override
    public long getAttackDuration() {
        return attackDurationDetector.getAttackDuration();
    }

    @Override
    public DynamicCounterThread getDynamicJoins(){
        return joinPerSecond;
    }

    @Override
    public DynamicCounterThread getDynamicPings() {
        return pingPerSecond;
    }

    @Override
    public DynamicCounterThread getDynamicPackets() {
        return packetPerSecond;
    }

    @Override
    public BlackListService getBlackListService() {
        return blackListService;
    }

    @Override
    public QueueService getQueueService() {
        return queueService;
    }

    @Override
    public WhitelistService getWhitelistService() {
        return whitelistService;
    }

    @Override
    public ModeType getModeType() {
        return modeType;
    }

    @Override
    public void setModeType(ModeType type) {
        this.modeType = type;
    }

    @Override
    public void disableAll() {
        this.isAntiBotModeOnline = false;
        this.isSlowAntiBotModeOnline = false;
        this.isPacketModeEnabled = false;
        this.isPingModeEnabled = false;
        this.modeType = ModeType.OFFLINE;
    }

    @Override
    public void disableMode(ModeType type) {
        if (type.equals(ModeType.ANTIBOT)) {
            this.isAntiBotModeOnline = false;
        }
        if (type.equals(ModeType.SLOW)) {
            this.isSlowAntiBotModeOnline = false;
        }
        if (type.equals(ModeType.PACKETS)) {
            this.isPacketModeEnabled = false;
        }
        if (type.equals(ModeType.PING)) {
            this.isPingModeEnabled = false;
        }

        this.modeType = ModeType.OFFLINE;
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.OFFLINE));
    }

    @Override
    public boolean isSomeModeOnline() {
        return isAntiBotModeOnline || isSlowAntiBotModeOnline || isPacketModeEnabled || isPingModeEnabled;
    }

    @Override
    public void increaseJoinPerSecond() {
        joinPerSecond.increase();
        increaseConnectionPerSecond();
    }

    @Override
    public void increasePingPerSecond() {
        pingPerSecond.increase();
        increaseConnectionPerSecond();
    }

    @Override
    public void increasePacketPerSecond() {
        packetPerSecond.increase();
        increaseConnectionPerSecond();
    }

    @Override
    public void increaseConnectionPerSecond(){
        connectionPerSecond.increase();
    }

    @Override
    public boolean isAntiBotModeEnabled() {
        return isAntiBotModeOnline;
    }

    @Override
    public boolean isSlowAntiBotModeEnabled() {
        return isSlowAntiBotModeOnline;
    }

    @Override
    public boolean isPacketModeEnabled() {
        return isPacketModeEnabled;
    }

    @Override
    public boolean isPingModeEnabled() {
        return isPingModeEnabled;
    }

    @Override
    public void enableAntiBotMode() {
        setModeType(ModeType.ANTIBOT);
        isAntiBotModeOnline = true;
        isSlowAntiBotModeOnline = false;
        isPingModeEnabled = false;
        isPacketModeEnabled = false;
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.ANTIBOT));
    }

    @Override
    public void enableSlowAntiBotMode() {
        setModeType(ModeType.SLOW);
        isAntiBotModeOnline = false;
        isSlowAntiBotModeOnline = true;
        isPingModeEnabled = false;
        isPacketModeEnabled = false;
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.SLOW));
    }

    @Override
    public void enablePacketMode() {
        setModeType(ModeType.PACKETS);
        isAntiBotModeOnline = false;
        isSlowAntiBotModeOnline = false;
        isPingModeEnabled = false;
        isPacketModeEnabled = true;
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.PACKETS));
    }

    @Override
    public void enablePingMode() {
        setModeType(ModeType.PING);
        isAntiBotModeOnline = false;
        isSlowAntiBotModeOnline = false;
        isPingModeEnabled = true;
        isPacketModeEnabled = false;
        EventCaller.call(new ModeEnableEvent(iAntiBotPlugin, ModeType.PING));
    }

    @Override
    public void dispatchConsoleAttackMessage() {
        if (isAntiBotModeOnline || isPingModeEnabled || isSlowAntiBotModeOnline) {
            logHelper.info(replaceInfo(MessageManager.actionbarAntiBotMode.replace("%prefix%", "")));
        }else{
            if(isPacketModeEnabled){
                logHelper.info(replaceInfo(MessageManager.actionbarPackets.replace("%prefix%", "")));
            }
        }
        checkModeDisable();
    }

    public void checkModeDisable() {
        if(modeType.equals(ModeType.OFFLINE)) return;
        long time = attackDurationDetector.getAttackDuration();
        boolean canDisable = canDisable(modeType);
        boolean timePasted = time > modeType.getKeepValue();

        if(canDisable && timePasted) {
            EventCaller.call(new AttackStateEvent(iAntiBotPlugin, AttackState.STOPPED, modeType));
            disableMode(modeType);
            ServerUtil.getInstance().getLogHelper().debug("[ANTIBOT MANAGER] Approved mode disabling for mode " + modeType);
        }

        if (timePasted) {
            attackDurationDetector.resetDuration();
            EventCaller.call(new AttackStateEvent(iAntiBotPlugin, AttackState.RUNNING, modeType));
            ServerUtil.getInstance().getLogHelper().debug("[ANTIBOT MANAGER] Refused mode disabling for mode " + modeType);
        }


        int priority = -1;
        ModeType selected = null;

        for (ModeType mode : ModeType.values()) {
            if (mode.checkConditions(joinPerSecond.getSlowCount(), pingPerSecond.getSlowCount(), packetPerSecond.getSlowCount())) {
                int prior = mode.getPriority();
                if (prior > priority) {
                    priority = prior;
                    selected = mode;
                }
            }
        }

        if(selected == null || selected.equals(modeType)) {
            return;
        }

        switch (selected) {
            case ANTIBOT:
                enableAntiBotMode();
                break;
            case PACKETS:
                enablePacketMode();
                break;
            case PING:
                enablePingMode();
                break;
        }
    }

    @Override
    public boolean canDisable(ModeType modeType) {
        if (modeType.equals(ModeType.ANTIBOT) || modeType.equals(ModeType.SLOW)) {
            ServerUtil.getInstance().getLogHelper().debug("[ANTIBOT MANAGER] Requested mode disabling for " + modeType.name() + " result " + (joinPerSecond.getSlowCount() <= ConfigManger.antiBotModeTrigger));
            return joinPerSecond.getSlowCount() <= ConfigManger.antiBotModeTrigger;
        }
        if (modeType.equals(ModeType.PING)) {
            ServerUtil.getInstance().getLogHelper().debug("[ANTIBOT MANAGER] Requested mode disabling for " + modeType.name() + " result " + (pingPerSecond.getSlowCount() <= ConfigManger.pingModeTrigger));
            return pingPerSecond.getSlowCount() <= ConfigManger.pingModeTrigger;
        }
        if (modeType.equals(ModeType.PACKETS)) {
            ServerUtil.getInstance().getLogHelper().debug("[ANTIBOT MANAGER] Requested mode disabling for " + modeType.name() + " result " + (packetPerSecond.getSlowCount() < ConfigManger.packetModeTrigger));
            return packetPerSecond.getSlowCount() < ConfigManger.packetModeTrigger;
        }
        ServerUtil.getInstance().getLogHelper().debug("[ANTIBOT MANAGER] Requested mode disabling for " + modeType);
        return false;
    }

    @Override
    public JoinCache getJoinCache() {
        return joinCache;
    }

    @Override
    public AttackWatcher getAttackWatcher() {
        return attackWatcher;
    }

    @Override
    public String replaceInfo(String str) {
        return str
                .replace("%bots%", String.valueOf(joinPerSecond.getSlowCount()))
                .replace("%pings%", String.valueOf(pingPerSecond.getSlowCount()))
                .replace("%queue%", String.valueOf(queueService.size()))
                .replace("%whitelist%", String.valueOf(whitelistService.size()))
                .replace("%blacklist%", String.valueOf(blackListService.size()))
                .replace("%type%", String.valueOf(modeType.toString()))
                .replace("%packets%", String.valueOf(packetPerSecond.getSlowCount()))
                .replace("%totalbots%", String.valueOf(Formatter.format(joinPerSecond.getTotal())))
                .replace("%totalpings%", String.valueOf(Formatter.format(pingPerSecond.getTotal())))
                .replace("%totalpackets%", String.valueOf(Formatter.format(packetPerSecond.getTotal())))
                .replace("%latency%", iAntiBotPlugin.getLatencyThread().getLatency())
                .replace("%prefix%", iAntiBotPlugin.getAnimationThread().getEmote() + " " + MessageManager.prefix)
                .replace("%underverification%", String.valueOf(VPNService.getUnderVerificationSize()))
                .replace("%attack-types%", attackWatcher.getFiredAttacks().toString())
                ;
    }
}
