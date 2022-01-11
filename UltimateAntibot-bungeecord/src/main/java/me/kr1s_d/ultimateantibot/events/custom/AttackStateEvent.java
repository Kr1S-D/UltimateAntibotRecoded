package me.kr1s_d.ultimateantibot.events.custom;


import me.kr1s_d.ultimateantibot.common.objects.enums.AttackState;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import net.md_5.bungee.api.plugin.Event;

import java.lang.ref.PhantomReference;

public class AttackStateEvent extends Event {

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
}
