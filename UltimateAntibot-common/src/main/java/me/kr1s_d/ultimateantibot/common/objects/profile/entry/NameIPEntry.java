package me.kr1s_d.ultimateantibot.common.objects.profile.entry;

import java.util.ArrayList;
import java.util.List;

public class NameIPEntry {
    private String ip;
    private List<String> names;

    public NameIPEntry(String ip, String fistName) {
        this.ip = ip;
        this.names = new ArrayList<>();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void registerName(String str) {
        if(names.contains(str)) return;
        names.add(str);
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
