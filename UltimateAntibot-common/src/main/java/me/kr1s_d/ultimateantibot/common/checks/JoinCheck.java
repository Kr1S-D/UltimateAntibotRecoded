package me.kr1s_d.ultimateantibot.common.checks;

import me.kr1s_d.ultimateantibot.common.UnderAttackMethod;

public interface JoinCheck extends Check {
    @UnderAttackMethod
    boolean isDenied(String ip, String name);
}
