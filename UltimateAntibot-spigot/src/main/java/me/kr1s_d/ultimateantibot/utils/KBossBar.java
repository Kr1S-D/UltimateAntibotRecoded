package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.common.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class KBossBar {

    private BossBar bar;
    private final boolean isCreated;

    public KBossBar(){
        if(Version.getBukkitServerVersion() < 19) {
            this.isCreated = false;
            return;
        }
        this.isCreated = true;
        bar = Bukkit.createBossBar(Utils.colora("&fWaiting for a new attack!"), BarColor.RED, BarStyle.SOLID);
    }


    public void addPlayer(Player player) {
        if(!isCreated) return;
        bar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        if(!isCreated) return;
        bar.removePlayer(player);
    }

    public List<Player> getPlayers() {
        return bar.getPlayers();
    }

    public void setTitle(String colorize) {
        bar.setTitle(colorize);
    }

    public void setProgress(float health) {
        bar.setProgress(health);
    }

    public boolean isCreated() {
        return isCreated;
    }
}
