package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.StaticCheck;
import me.kr1s_d.ultimateantibot.common.objects.LimitedList;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.MessageEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.NickNameEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ContainerType;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.MetadataContainer;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ScoreTracker;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is not a real check, it helps detect slow bot attacks and is inserted into different
 * parts of the various events which are reported below (to remind)
 *
 * UserDataService#isFirstJoin
 * PacketCheck
 *
 * Methods will be considered as static, and will be called in the MainEventListener with CheckService
 * Instantiation is executed in the main classes
 */
public class ConnectionAnalyzerCheck implements StaticCheck {
    private IAntiBotPlugin plugin;
    private IAntiBotManager antiBotManager;
    private UserDataService userDataService;
    private WhitelistService whitelistService;
    private MetadataContainer<ConnectionProfile> chatSuspected;

    public ConnectionAnalyzerCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.userDataService = plugin.getUserDataService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.chatSuspected = new MetadataContainer<>(false);

        CheckService.register(this);
        if(isEnabled()) {
            plugin.getLogHelper().debug("Loaded " + this.getClass().getSimpleName() + "!");
        }
    }

    public void checkJoined() {
        List<ConnectionProfile> last = userDataService.getLastJoinedAndConnectedProfiles(15);
        List<ConnectionProfile> suspected = new ArrayList<>();

        for (ConnectionProfile profile : last) {
            if (whitelistService.isWhitelisted(profile.getIP())) continue;

            LimitedList<NickNameEntry> nickHistory = profile.getLastNickNames();

            for (NickNameEntry currentNickname : nickHistory) {
                for (ConnectionProfile otherProfile : last) {
                    if (profile == otherProfile) continue; // Salta se controlla la stessa connessione

                    LimitedList<NickNameEntry> otherNickHistory = otherProfile.getLastNickNames();

                    for (NickNameEntry otherNickname : otherNickHistory) {
                        // Confronta i nickname
                        if (StringUtil.calculateSimilarity(currentNickname.getName(), otherNickname.getName()) > 80) {
                            // Aggiunge le connessioni con nickname uguali alla lista
                            suspected.add(profile);
                            suspected.add(otherProfile);
                        }
                    }
                }
            }
        }

        if(suspected.size() > ConfigManger.connectionAnalyzeNameTrigger) {
            for (ConnectionProfile profile : suspected) {
                profile.process(ScoreTracker.ScoreID.ABNORMAL_NAME);
            }
        }
    }

    public void onChat(String ip, String nickname, String message) {
        ConnectionProfile profile = userDataService.getProfile(ip);
        profile.trackChat(message);
        List<ConnectionProfile> last = userDataService.getLastJoinedAndConnectedProfiles(15);

        List<String> entries = last.stream()
                .filter(p -> !p.getIP().equals(ip)) //ignore self messages
                .map(ConnectionProfile::getChatMessages)
                .collect(ArrayList::new, (a, b) -> b.forEach(msg -> a.add(msg.getIP())), (a, b) -> {});
        if(entries.isEmpty() || entries.size() > 10) return; //skip division for 0 and useless check

        for (String entry : entries) {
            if (StringUtil.spaces(message) < 2 && message.length() < 5) continue; //ignore small messages
            if (StringUtil.calculateSimilarity(message, entry) > 85) {
                chatSuspected.incrementInt(profile, 0);
            }
        }
        double percent = (double) chatSuspected.getOrDefaultNoPut(profile, Integer.class, 0) / ((double) entries.size()) * 100D;

        if (percent >= ConfigManger.connectionAnalyzeChatTrigger) {
            profile.process(ScoreTracker.ScoreID.ABNORMAL_CHAT_MESSAGE);
        }

        plugin.getLogHelper().debug("[CONNECTION ANALYZER] Chat percent for " + nickname + " is " + percent);
    }

    public void onPing(String ip) {
        userDataService.getProfile(ip).trackPing();
    }

    @Override
    public void onDisconnect(String ip, String name) {
        //processed in userdataservice
    }

    @Override
    public CheckType getType() {
        return CheckType.CONNECTION_ANALYZE;
    }

    @Override
    public boolean isEnabled() {
        return ConfigManger.isConnectionAnalyzeEnabled;
    }

    @Override
    public long getCacheSize() {
        return chatSuspected.size();
    }

    @Override
    public void clearCache() {
        chatSuspected.clear();
    }

    @Override
    public void removeCache(String ip) {
        chatSuspected.removeIf(i -> i.getIP().equals(ip));
    }
}
