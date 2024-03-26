package me.kr1s_d.ultimateantibot.common.objects.profile.meta;

public enum ContainerType {
    JOIN_INFO(true),
    KNOWN_NICKNAMES_IP(true),
    CHAT_HISTORY(false);

    private boolean persistence;

    ContainerType(boolean persistence) {
        this.persistence = persistence;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }
}
