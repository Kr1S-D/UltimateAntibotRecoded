package me.kr1s_d.ultimateantibot.common.objects.profile;

import java.util.UUID;

public class BlackListProfile {
    private final String id;
    private final String name;
    private final String reason;
    private final String ip;

    /**
     * @param reason Reason for BlackList
     */
    public BlackListProfile(String ip, String reason){
        this.ip = ip;
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.name = "_NAME_NOT_FOUND_";
        this.reason = reason;
    }

    /**
     * @param reason Reason for BlackList
     * @param id The ID for check
     */
    public BlackListProfile(String ip, String reason, String id, String name){
        this.id = id;
        this.reason = reason;
        this.name = name;
        this.ip = ip;
    }

    /**
     * @param reason Reason for BlackList
     * @param name The name of the player
     */
    public BlackListProfile(String ip, String reason, String name) {
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.reason = reason;
        this.name = name;
        this.ip = ip;
    }

    public boolean isNamePresent(){
        return name != null;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public String getIp() {
        return ip;
    }
}
