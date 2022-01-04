package me.kr1s_d.ultimateantibot.common.objects.other;

import java.util.UUID;

public class BlackListProfile {
    private final String id;
    private final String name;
    private final String reason;

    /**
     * @param reason Reason for BlackList
     */
    public BlackListProfile(String reason){
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.name = null;
        this.reason = reason;
    }

    /**
     * @param reason Reason for BlackList
     * @param id The ID for check
     */
    public BlackListProfile(String reason, String id, String name){
        this.id = id;
        this.reason = reason;
        this.name = name;
    }

    /**
     * @param reason Reason for BlackList
     * @param name The name of the player
     */
    public BlackListProfile(String reason, String name) {
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.reason = reason;
        this.name = name;
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
}
