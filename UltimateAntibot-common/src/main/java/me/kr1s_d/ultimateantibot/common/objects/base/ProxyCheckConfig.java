package me.kr1s_d.ultimateantibot.common.objects.base;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

public class ProxyCheckConfig {
    private final String key;
    private final boolean checkSlowJoin;
    private final boolean checkFastJoin;

    public ProxyCheckConfig(IConfiguration config) {
        this.key = config.getString("proxycheck.api-key");
        this.checkSlowJoin = config.getBoolean("proxycheck.check-slow-join");
        this.checkFastJoin = config.getBoolean("proxycheck.checj-fast-join");
    }

    public String getKey() {
        return key;
    }

    public boolean isCheckSlowJoin() {
        return checkSlowJoin;
    }

    public boolean isCheckFastJoin() {
        return checkFastJoin;
    }

    public boolean isEnabled(){
        return key.length() == 27;
    }
}
