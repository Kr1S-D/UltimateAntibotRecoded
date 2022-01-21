package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import me.kr1s_d.ultimateantibot.common.objects.other.BlackListProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackListService implements IService {

    private final Map<String, BlackListProfile> blacklist;
    private final IConfiguration blacklistConfig;
    private final LogHelper logHelper;

    /**
     *
     * @param blacklistConfig - IConfiguration for BlackList Service
     * @param logHelper - LogHelper for debug
     */
    public BlackListService(IConfiguration blacklistConfig, LogHelper logHelper){
        this.blacklist = new HashMap<>();
        this.blacklistConfig = blacklistConfig;
        this.logHelper = logHelper;
    }

    /**
     * Adapt / DeAdapt IP Strings To File Saving...
     */

    private String toFileString(String str){
        return str.replace(".", ",");
    }

    private String toIp(String str){
        return str.replace(",", ".");
    }

    /**
     * Loading Configuration From File
     */
    @Override
    public void load() {
        for(String a : blacklistConfig.getConfigurationSection("data")){
            String ip = toIp(a);
            String reason = blacklistConfig.getString("data." + a + ".reason");
            String id = blacklistConfig.getString("data." + a + ".id");
            String name = blacklistConfig.getString("data." + a + ".name");
            blacklist.put(ip, new BlackListProfile(ip, reason, id, name));
        }
        logHelper.info("&f" + blacklist.size() + " &dIP added to blacklist!");
    }

    @Override
    public void unload() {
        blacklistConfig.set("data", null);
        for(Map.Entry<String, BlackListProfile> map : blacklist.entrySet()){
            String ip = toFileString(map.getKey());
            String reason = map.getValue().getReason();
            String id = map.getValue().getId();
            blacklistConfig.set("data." + ip + ".reason", reason);
            blacklistConfig.set("data." + ip + ".id", id);
            blacklistConfig.set("data." + ip + ".name", map.getValue().getName());
        }
        blacklistConfig.save();
    }

    public List<String> getBlackListedIPS(){
        List<String> a = new ArrayList<>();
        new HashMap<>(blacklist).forEach((key, value) -> a.add(key));
        return a;
    }


    public int size(){
        return blacklist.size();
    }

    /**
     *
     * @param ip The IP to BlackList
     * @param reason The Reason for BlackList
     * @param name The name of the player
     */
    public void blacklist(String ip, String reason, String name){
        if(blacklist.containsKey(ip)){
            return;
        }
        blacklist.put(ip, new BlackListProfile(ip, reason, name));
    }

    /**
     *
     * @param ip The IP to BlackList
     * @param reason The Reason for BlackList
     */
    public void blacklist(String ip, String reason){
        if(blacklist.containsKey(ip)){
            return;
        }
        blacklist.put(ip, new BlackListProfile(ip, reason));
    }

    public void clear(){
        blacklist.clear();
    }

    public void unBlacklist(String ip){
        blacklist.remove(ip);
    }

    public boolean isBlackListed(String ip){
        return blacklist.containsKey(ip);
    }

    public BlackListProfile getProfile(String ip){
        return blacklist.get(ip);
    }

    /**
     * @param id The Blacklist ID
     * @return Ip of the Player
     */
    public String getIPFromID(String id){
        for(Map.Entry<String, BlackListProfile> map : blacklist.entrySet()){
            if(map.getValue().getId().equals(id)){
                return map.getKey();
            }
        }
        return null;
    }

    /**
     * @param id The Blacklist ID
     * @return BlackListProfile of the Player
     */
    public BlackListProfile getBlacklistProfileFromID(String id){
        for(Map.Entry<String, BlackListProfile> map : blacklist.entrySet()){
            if(map.getValue().getId().equals(id)){
                return map.getValue();
            }
        }
        return null;
    }
}
