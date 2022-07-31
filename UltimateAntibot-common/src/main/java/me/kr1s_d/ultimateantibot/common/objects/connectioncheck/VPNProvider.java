package me.kr1s_d.ultimateantibot.common.objects.connectioncheck;

public interface VPNProvider {
    String getID();

    void process(String ip, String name);

    String getCountry(String ip, String name);
}
