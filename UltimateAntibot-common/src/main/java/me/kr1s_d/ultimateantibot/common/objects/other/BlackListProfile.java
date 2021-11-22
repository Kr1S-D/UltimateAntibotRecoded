package me.kr1s_d.ultimateantibot.common.objects.other;

import java.util.UUID;

public class BlackListProfile {
    private final String id;
    private final String reason;

    /**
     * @param reason Reason for BlackList
     */
    public BlackListProfile(String reason){
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.reason = reason;
    }

    /**
     * @param reason Reason for BlackList
     * @param id The ID for check
     */
    public BlackListProfile(String reason, String id){
        this.id = id;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }
}
