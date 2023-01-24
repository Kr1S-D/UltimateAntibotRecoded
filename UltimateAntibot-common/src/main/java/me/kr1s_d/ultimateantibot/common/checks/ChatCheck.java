package me.kr1s_d.ultimateantibot.common.checks;

public interface ChatCheck extends Check {
    void onChat(String ip, String nickname, String message);

    void onTabComplete(String ip, String nickname, String message);
}
