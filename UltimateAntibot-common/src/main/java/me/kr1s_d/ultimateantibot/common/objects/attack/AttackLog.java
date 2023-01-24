package me.kr1s_d.ultimateantibot.common.objects.attack;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.utils.Formatter;
import me.kr1s_d.ultimateantibot.common.utils.TimeUtil;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class AttackLog implements Serializable {
    private final int ID;
    private final String attackDate;
    private long previousBlacklist, previousBots, previousPings, previousPackets = 0;
    private long newBlacklist = 0;
    private long blockedBots = 0;
    private long blockedPings = 0;
    private long blockedPackets = 0;
    private long startMillis;
    private long stopMillis = 0;
    private long attackDuration = 0;

    public AttackLog(int ID, String attackDate) {
        this.ID = ID;
        this.attackDate = attackDate;
    }

    public void recordStart(long currentBlacklist, IAntiBotManager manager) {
        previousBlacklist = currentBlacklist;
        previousBots = manager.getDynamicJoins().getTotal();
        previousPings = manager.getDynamicPings().getTotal();
        previousPackets = manager.getDynamicPackets().getTotal();
        this.startMillis = System.currentTimeMillis();
    }

    public void recordStop(long newBlacklist, IAntiBotManager manager) {
        this.newBlacklist = newBlacklist;
        blockedBots = manager.getDynamicJoins().getTotal() - previousBots;
        blockedPings = manager.getDynamicPings().getTotal() - previousPings;
        blockedPackets = manager.getDynamicPackets().getTotal() - previousPackets;
        this.stopMillis = System.currentTimeMillis();
        this.attackDuration = stopMillis - startMillis;
    }

    public int getID() {
        return ID;
    }

    public String getAttackDate() {
        return attackDate;
    }

    public long getPreviousBlacklist() {
        return previousBlacklist;
    }

    public long getNewBlacklist() {
        return newBlacklist;
    }

    public long getBlockedBots() {
        return blockedBots;
    }

    public long getBlockedPings() {
        return blockedPings;
    }

    public long getBlockedPackets() {
        return blockedPackets;
    }

    public long getStartMillis() {
        return startMillis;
    }
    public long getStopMillis() {
        return stopMillis;
    }

    public long getAttackDuration() {
        return attackDuration;
    }

    public AttackPower getAttackPower() {
        long sum = getAverageConnections();
        if(sum > 80000) {
            return AttackPower.INCREDIBLE;
        }
        if(sum > 40000) {
            return AttackPower.HUGE;
        }
        if(sum > 25000) {
            return AttackPower.LARGE;
        }
        if(sum > 15000) {
            return AttackPower.HIGH;
        } 
        if(sum > 5000) {
            return AttackPower.MEDIUM;
        }
        return AttackPower.LOW;
    }

    public long getAverageConnections(){
        long duration = TimeUnit.MILLISECONDS.toSeconds(attackDuration);
        long removal = 15 * duration / 100;
        duration -= removal;
        long total = blockedBots + blockedPings + blockedPackets;
        return total / duration;
    }

    public String replaceInformation(String message){
        return message.replace("%avg%", Formatter.format(getAverageConnections())).replace("%power%", getAttackPower().name()).replace("%duration%", TimeUtil.formatMilliseconds(attackDuration)).replace("%packets%", Formatter.format(blockedPackets)).replace("%pings%", Formatter.format(getBlockedPings())).replace("%bots%", Formatter.format(getBlockedBots())).replace("%blacklist%", Formatter.format(newBlacklist - previousBlacklist)).replace("%attack%", attackDate).replace("%id%", String.valueOf(getID()));
    }
}
