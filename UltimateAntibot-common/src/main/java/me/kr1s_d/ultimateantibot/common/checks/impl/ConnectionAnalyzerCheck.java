package me.kr1s_d.ultimateantibot.common.checks.impl;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.StaticCheck;
import me.kr1s_d.ultimateantibot.common.objects.LimitedList;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
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

    public ConnectionAnalyzerCheck(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.userDataService = plugin.getUserDataService();
        this.whitelistService = antiBotManager.getWhitelistService();

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
        userDataService.getProfile(ip).trackChat(message);

        /*List<ConnectionProfile> last = userDataService.getLastJoinedAndConnectedProfiles(15);
        MetadataContainer<ConnectionProfile> suspected = new MetadataContainer<>(false);

        for (int i = 0; i < last.size(); i++) {
            ConnectionProfile profile1 = last.get(i);
            if (whitelistService.isWhitelisted(profile1.getIP())) continue;

            LimitedList<String> messages1 = profile1.getChatMessages();

            for (int j = i + 1; j < last.size(); j++) {
                ConnectionProfile profile2 = last.get(j);
                if (whitelistService.isWhitelisted(profile2.getIP())) continue;

                LimitedList<String> messages2 = profile2.getChatMessages();

                // Controlla la similarità tra i messaggi dei due profili
                for (String msg1 : messages1) {
                    for (String msg2 : messages2) {
                        if (StringUtil.calculateSimilarity(msg1, msg2) > 80) {
                            suspected.incrementInt(profile2, 0);
                            suspected.incrementInt(profile1, 0);
                        }
                    }
                }
            }
        }

        if (suspected.size() >= 3) {
            for (ConnectionProfile profile : suspected) {
                if(suspected.getOrDefaultNoPut(profile, Integer.class, 0) >= ConfigManger.connectionAnalyzeChatTrigger) {
                    profile.process(ScoreTracker.ScoreID.ABNORMAL_CHAT_MESSAGE);
                }
            }
        }*/
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
        return userDataService.size();
    }

    @Override
    public void clearCache() {
        //unsupported here
    }

    @Override
    public void removeCache(String ip) {
        //unsupported here
    }
}
