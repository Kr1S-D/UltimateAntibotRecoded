package me.kr1s_d.ultimateantibot.common.checks;

public interface JoinCheck extends Check {
    boolean isDenied(String ip, String name);
}
