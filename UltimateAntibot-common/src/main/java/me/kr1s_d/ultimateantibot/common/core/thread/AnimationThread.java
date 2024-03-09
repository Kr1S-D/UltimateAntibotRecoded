package me.kr1s_d.ultimateantibot.common.core.thread;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

import java.util.HashMap;
import java.util.Map;

public class AnimationThread {

    private final Map<Integer, String> animationMap;
    private String currentEmote;
    private int counter;

    public AnimationThread(IAntiBotPlugin antiBotPlugin){
        antiBotPlugin.getLogHelper().debug("Enabled AnimationThread!");
        this.counter = 1;
        this.animationMap = new HashMap<>();
        initializeAnimationMap();

        antiBotPlugin.scheduleRepeatingTask(this::updateEmote, true, 125L);
    }

    private void initializeAnimationMap() {
        animationMap.put(1, "▛");
        animationMap.put(2, "▜");
        animationMap.put(3, "▟");
        animationMap.put(4, "▙");
    }

    private void updateEmote() {
        currentEmote = animationMap.get(counter);
        counter = (counter % 4) + 1;
    }


    public String getEmote() {
        return currentEmote;
    }
}
