package me.kr1s_d.ultimateantibot.common.objects.user;

public class PlayerProfile {
    private String name;
    private String uuid;
    private String ip;
    private long joins;

    public PlayerProfile(String name, String uuid, String ip, long joins) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.joins = joins;
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }

    public long getJoins() {
        return joins;
    }

    public boolean isSimilar(PlayerProfile profile){
        return uuid.equals(profile.uuid);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setJoins(long joins) {
        this.joins = joins;
    }

}
