package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import java.util.HashMap;
import java.util.Map;

public class AnimationThread {

    private final Map<Integer, String> animationMap;
    private String emote;
    private int increase;

    public AnimationThread(IAntiBotPlugin iAntiBotPlugin){
        iAntiBotPlugin.getLogHelper().debug("Enabled AnimationThread!");
        this.increase = 1;
        this.animationMap = new HashMap<>();
        animationMap.put(1, "▛");
        animationMap.put(2, "▜");
        animationMap.put(3, "▟");
        animationMap.put(4, "▙");
        iAntiBotPlugin.scheduleRepeatingTask(() -> {
            emote = animationMap.get(increase);
            if(increase == 4) this.increase = 0;
            increase++;
        }, true, 125L);
    }

    public String getEmote() {
        return emote;
    }
}
