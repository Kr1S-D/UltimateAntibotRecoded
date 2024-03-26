package me.kr1s_d.ultimateantibot.common.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IService;
import me.kr1s_d.ultimateantibot.common.UnderAttackMethod;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.checks.impl.ConnectionAnalyzerCheck;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ScoreTracker;
import me.kr1s_d.ultimateantibot.common.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UserDataService implements IService {
    private final IAntiBotPlugin plugin;
    private final LogHelper logHelper;
    private Cache<String, ConnectionProfile> profiles;
    private List<ConnectionProfile> onlineProfiles;

    public UserDataService(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.profiles = Caffeine.newBuilder()
                .build();
        this.onlineProfiles = new ArrayList<>();
    }

    @Override
    public void load() {
        try {
            String encodedConnections = FileUtil.getEncodedBase64("profiles.dat", FileUtil.UABFolder.DATA);
            if (encodedConnections != null) {
                //load saved data as list
                List<ConnectionProfile> serialized = SerializeUtil.deserialize(encodedConnections, ArrayList.class);
                //put inside cache
                if (serialized != null) {
                    for (ConnectionProfile profile : serialized) {
                        profiles.put(profile.getIP(), profile);
                    }
                }
            }

            //remove olds
            int count = 0;
            for (Map.Entry<String, ConnectionProfile> map : profiles.asMap().entrySet()) {
                if(map.getValue().getDaysFromLastJoin() >= 30) {
                    profiles.invalidate(map.getKey());
                    count++;
                }
            }

            profiles.cleanUp();
            plugin.getLogHelper().info("&c" + profiles.estimatedSize() + " &fconnection profiles loaded!");
            plugin.getLogHelper().info("&c" + count + " &fconnection profiles removed for inactivity!");
        } catch (Exception e) {
            FileUtil.renameFile("profiles.dat", FileUtil.UABFolder.DATA, "corrupted-old.dat");
            logHelper.error("Unable to load serialized files! If error persists contact support please!");
        }
    }

    @Override
    public void unload() {
        //convert cache to a list of connectionprofile
        List<ConnectionProfile> profiles = new ArrayList<>();
        //fill list with current data
        this.profiles.asMap().forEach((key, value) -> {
            value.checkMetadata();
            profiles.add(value);
        });
        //serialize
        FileUtil.writeBase64("profiles.dat", FileUtil.UABFolder.DATA, profiles);
    }

    public void registerJoin(String ip, String nickname) {
        ConnectionProfile profile = getProfile(ip);
        profile.trackJoin(nickname);
        onlineProfiles.add(profile);
        profile.process(ScoreTracker.ScoreID.JOIN_NO_PING);
        CheckService.getCheck(CheckType.CONNECTION_ANALYZE, ConnectionAnalyzerCheck.class).checkJoined();
    }

    public void registerQuit(String ip) {
        ConnectionProfile profile = profiles.getIfPresent(ip);
        if(profile == null) return;
        profile.trackDisconnect();
        onlineProfiles.remove(profile);
    }

    @UnderAttackMethod
    public void resetFirstJoin(String ip) {
        ConnectionProfile profile = profiles.getIfPresent(ip);
        if(profile == null) return;
        profile.setFirstJoin(true);
    }

    @UnderAttackMethod
    public boolean isFirstJoin(String ip, String nickname) {
        ConnectionProfile profile = profiles.get(ip, k -> new ConnectionProfile(ip));
        if(profile == null) return true;
        if(profile.isFirstJoin()) {
            //while listening for first join start checking for connection analyze
            getProfile(ip).process(ScoreTracker.ScoreID.IS_FIST_JOIN, true);
            profile.setFirstJoin(false);
            return true;
        }

        return false;
    }

    @UnderAttackMethod
    public ConnectionProfile getProfile(String ip) {
        return profiles.get(ip, k -> new ConnectionProfile(ip));
    }

    public void tickProfiles() {
        getConnectedProfiles().forEach(ConnectionProfile::tickMinute);
    }

    public void checkBots() {
        if(!ConfigManger.isConnectionAnalyzeEnabled) {
            return;
        }

        List<ConnectionProfile> bots = getConnectedProfilesByScore(ConfigManger.connectionAnalyzeBlacklistFrom);
        if(bots.size() >= ConfigManger.connectionAnalyzeBlacklistTrigger) {
            disconnectProfiles(ConfigManger.connectionAnalyzeBlacklistFrom, true);
            plugin.getAntiBotManager().enableSlowAntiBotMode();
        }
    }

    public int size() {
        return (int) profiles.estimatedSize();
    }

    @UnderAttackMethod
    public List<ConnectionProfile> getConnectedProfilesByScore(ConnectionProfile.ConnectionScore score) {
        int ordinal = score.ordinal();
        return getConnectedProfiles().stream()
                .filter(s -> s.getConnectionScore().ordinal() >= ordinal)
                .collect(Collectors.toList());
    }

    @UnderAttackMethod
    public void disconnectProfiles(ConnectionProfile.ConnectionScore score, boolean blacklist) {
        for (ConnectionProfile profile : getConnectedProfilesByScore(score)) {
            profile.setFirstJoin(true); //reset first join check
            plugin.disconnect(profile.getIP(), MessageManager.getSafeModeMessage());

            if(blacklist) {
                plugin.getAntiBotManager().getBlackListService().blacklist(profile.getIP(), BlackListReason.STRANGE_PLAYER_CONNECTION, profile.getCurrentNickName());
            }
        }
    }

    public List<ConnectionProfile> getProfiles() {
        List<ConnectionProfile> profiles = new ArrayList<>();
        this.profiles.asMap().forEach((key, value) -> profiles.add(value));
        return profiles;
    }

    @UnderAttackMethod
    public List<ConnectionProfile> getLastJoinedAndConnectedProfiles(int minutes) {
        return getConnectedProfiles().stream()
                .filter(s -> s.getSecondsFromLastJoin() <= TimeUnit.MINUTES.toSeconds(minutes))
                .collect(Collectors.toList());
    }

    @UnderAttackMethod
    public List<ConnectionProfile> getConnectedProfiles() {
        return onlineProfiles;
    }
}
