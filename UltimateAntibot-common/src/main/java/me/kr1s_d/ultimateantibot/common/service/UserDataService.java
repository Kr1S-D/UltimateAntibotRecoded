package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import me.kr1s_d.ultimateantibot.common.objects.user.PlayerProfile;

import java.util.ArrayList;
import java.util.List;

public class UserDataService implements IService {
    private final List<PlayerProfile> loadedProfiles;
    private final IConfiguration database;
    private final LogHelper logHelper;

    public UserDataService(IConfiguration database, IAntiBotPlugin plugin){
        this.logHelper = plugin.getLogHelper();
        this.database = database;
        this.loadedProfiles = new ArrayList<>();
    }

    @Override
    public void load() {
        for (String UUID : database.getConfigurationSection("data")){
            final String name = database.getString("data." + UUID + ".name");
            final String ip = database.getString("data." + UUID + ".ip");
            final long join = database.getLong("data." + UUID + ".join");
            final boolean firstjoin = database.getBoolean("data." + UUID + ".firstjoin");
            loadedProfiles.add(new PlayerProfile(UUID, name, ip, join, firstjoin));
        }
    }

    @Override
    public void unload() {
        for(PlayerProfile profile : loadedProfiles){
            final String UUID = profile.getUUID();
            final String name = profile.getName();
            final String ip = profile.getIp();
            final long join = profile.getJoins();
            final boolean firstJoin = profile.isFirstJoin();
            database.set("data." + UUID + ".name", name);
            database.set("data." + UUID + ".ip", ip);
            database.set("data." + UUID + ".join", join);
            database.set("data." + UUID + ".isfirstjoin", firstJoin);
        }
        database.save();
    }

    public PlayerProfile getFromUUID(String uuid){
        for(PlayerProfile profile : loadedProfiles){
            if(uuid.equals(profile.getUUID())){
                return profile;
            }
        }
        return null;
    }

    public PlayerProfile getFromName(String name){
        for(PlayerProfile profile : loadedProfiles){
            if(profile.getName().equals(name)) return profile;
        }
        return null;
    }

    public PlayerProfile getFromIP(String ip){
        for(PlayerProfile profile : loadedProfiles){
            if(profile.getIp().equals(ip)) return profile;
        }
        return null;
    }

    public boolean isLoaded(PlayerProfile profile){
        return loadedProfiles.contains(profile);
    }

    public void loadNewUser(PlayerProfile profile){
        if(!isLoaded(profile)){
            loadedProfiles.add(profile);
            logHelper.debug("Adding new PlayerProfile " + profile.getName());
        }
    }
}
