package me.kr1s_d.ultimateantibot.common.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.kr1s_d.ultimateantibot.common.UnderAttackMethod;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IService;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.utils.FileUtil;
import me.kr1s_d.ultimateantibot.common.utils.SerializeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDataService implements IService {
    private final IAntiBotPlugin plugin;
    private final LogHelper logHelper;
    //private List<String> firstJoin;
    private Cache<String, ConnectionProfile> profiles;

    public UserDataService(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.logHelper = plugin.getLogHelper();
        //this.firstJoin = new ArrayList<>();
        this.profiles = Caffeine.newBuilder()
                .build();
    }

    @Override
    public void load() {
        try {
            //String encodedUsers = FileUtil.getEncodedBase64("users.dat", FileUtil.UABFolder.DATA);
            String encodedConnections = FileUtil.getEncodedBase64("profiles.dat", FileUtil.UABFolder.DATA);
            //if (encodedUsers == null && encodedConnections == null) {
            //    return;
            //}
            //if (encodedUsers != null) {
                //firstJoin = SerializeUtil.deserialize(encodedUsers, ArrayList.class);
                //if (firstJoin == null) firstJoin = new ArrayList<>();
            //}
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
            for (Map.Entry<String, ConnectionProfile> map : profiles.asMap().entrySet()) {
                if(map.getValue().getDaysFromLastJoin() >= 30) {
                    profiles.invalidate(map.getKey());
                }
            }
        } catch (Exception e) {
            //if (firstJoin == null) firstJoin = new ArrayList<>();
            logHelper.error("Unable to load serialized files! If error persists contact support please!");
        }
    }

    @Override
    public void unload() {
        //FileUtil.writeBase64("users.dat", FileUtil.UABFolder.DATA, firstJoin);
        //convert cache to a list of connectionprofile
        List<ConnectionProfile> profiles = new ArrayList<>();
        //fill list with current data
        this.profiles.asMap().forEach((key, value) -> profiles.add(value));
        //serialize
        FileUtil.writeBase64("profiles.dat", FileUtil.UABFolder.DATA, profiles);
    }

    public void registerJoin(String ip, String nickname) {
        ConnectionProfile profile = profiles.get(ip, k -> new ConnectionProfile(ip, nickname));
        profile.onJoin(ip);
    }

    public void registerQuit(String ip) {
        ConnectionProfile profile = profiles.getIfPresent(ip);
        if(profile == null) return;
        profile.onDisconnect();
    }

    @UnderAttackMethod
    public void resetFirstJoin(String ip) {
        ConnectionProfile profile = profiles.getIfPresent(ip);
        if(profile == null) return;
        profile.setFirstJoin(true);
    }

    @UnderAttackMethod
    public boolean isFirstJoin(String ip, String nickname) {
        ConnectionProfile profile = profiles.get(ip, k -> new ConnectionProfile(ip, nickname));

        if(profile.isFirstJoin()) {
            profile.setFirstJoin(false);
            return true;
        }

        return false;
    }

    public int size() {
        return (int) profiles.estimatedSize();
    }

    public List<ConnectionProfile> getProfiles() {
        List<ConnectionProfile> profiles = new ArrayList<>();
        this.profiles.asMap().forEach((key, value) -> profiles.add(value));
        return profiles;
    }
}
