package me.kr1s_d.ultimateantibot.common.antivpn;

public interface VPNProvider {
    String getID();

    void process(String ip, String name);

    String getCountry(String ip, String name);
}
