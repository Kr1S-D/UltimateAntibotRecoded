package me.kr1s_d.ultimateantibot.common.objects.profile.mapping;

import me.kr1s_d.ultimateantibot.common.objects.profile.entry.NameIPEntry;

import java.util.List;

public class IPMapping {
    private List<NameIPEntry> entryList;

    public IPMapping(List<NameIPEntry> entryList) {
        this.entryList = entryList;
    }

    public String getIPFromName(String name) {
        for (NameIPEntry nameIPEntry : entryList) {
            List<String> collect = nameIPEntry.getNames();
            for (String name2 : collect) {
                if(name2.equalsIgnoreCase(name)) return nameIPEntry.getIp();
            }
        }

        return null;
    }

    public List<NameIPEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<NameIPEntry> entryList) {
        this.entryList = entryList;
    }
}
