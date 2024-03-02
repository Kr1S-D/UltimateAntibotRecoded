package me.kr1s_d.ultimateantibot.event;

import me.kr1s_d.ultimateantibot.common.AttackState;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.ModeType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AttackStateEvent extends Event {

    private static final HandlerList handlerList =  new HandlerList();

    private IAntiBotPlugin plugin;
    private IAntiBotManager antiBotManager;
    private AttackState attackState;
    private ModeType attackTYPE;

    public AttackStateEvent(IAntiBotPlugin plugin, AttackState attackState, ModeType attackTYPE) {
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.attackState = attackState;
        this.attackTYPE = attackTYPE;
    }

    public ModeType getAttackTYPE() {
        return attackTYPE;
    }

    public void setAttackTYPE(ModeType attackTYPE) {
        this.attackTYPE = attackTYPE;
    }

    public IAntiBotPlugin getPlugin() {
        return plugin;
    }

    public IAntiBotManager getAntiBotManager() {
        return antiBotManager;
    }

    public AttackState getAttackState() {
        return attackState;
    }

    public void setPlugin(IAntiBotPlugin plugin) {
        this.plugin = plugin;
    }

    public void setAntiBotManager(IAntiBotManager antiBotManager) {
        this.antiBotManager = antiBotManager;
    }

    public void setAttackState(AttackState attackState) {
        this.attackState = attackState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
