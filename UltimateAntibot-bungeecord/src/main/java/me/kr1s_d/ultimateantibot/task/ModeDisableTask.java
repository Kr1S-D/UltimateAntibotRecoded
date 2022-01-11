package me.kr1s_d.ultimateantibot.task;

import me.kr1s_d.ultimateantibot.common.objects.enums.AttackState;
import me.kr1s_d.ultimateantibot.common.objects.enums.ModeType;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.events.custom.AttackStateEvent;
import me.kr1s_d.ultimateantibot.utils.EventCaller;
import me.kr1s_d.ultimateantibot.utils.Utils;

public class ModeDisableTask implements Runnable {

    private final IAntiBotPlugin antiBotPlugin;
    private final ModeType disableMode;

    public ModeDisableTask(IAntiBotPlugin antiBotPlugin, ModeType disableMode) {
        this.antiBotPlugin = antiBotPlugin;
        this.disableMode = disableMode;
    }

    @Override
    public void run() {
        if(antiBotPlugin.getAntiBotManager().canDisable(disableMode)){
            EventCaller.call(new AttackStateEvent(antiBotPlugin, AttackState.STOPPED, disableMode));
            antiBotPlugin.getAntiBotManager().disableMode(disableMode);
            return;
        }
        antiBotPlugin.scheduleDelayedTask(
                new ModeDisableTask(antiBotPlugin, disableMode),
                false,
                1000L * ConfigManger.antiBotModeKeep
        );
        EventCaller.call(new AttackStateEvent(antiBotPlugin, AttackState.RUNNING, disableMode));
    }
}
