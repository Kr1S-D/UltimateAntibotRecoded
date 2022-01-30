package me.kr1s_d.ultimateantibot.common.thread;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

import java.util.HashMap;
import java.util.Map;

public class AnimationThread {

    private final IAntiBotPlugin iAntiBotPlugin;
    private final Map<Integer, String> animationMap;
    private long speed;
    private String emote;
    private int increase;

    public AnimationThread(IAntiBotPlugin iAntiBotPlugin){
        iAntiBotPlugin.getLogHelper().debug("Enabled AnimationThread!");
        this.iAntiBotPlugin = iAntiBotPlugin;
        this.increase = 1;
        this.animationMap = new HashMap<>();
        speed = 1000L;
        animationMap.put(1, "▛");
        animationMap.put(2, "▜");
        animationMap.put(3, "▟");
        animationMap.put(4, "▙");
        new Thread(() -> {
            while (iAntiBotPlugin.isRunning()){
                emote = animationMap.get(increase);
                if(increase == 4) this.increase = 0;
                increase++;
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                calculateSpeed();
            }
        }, "UAB#AnimationThread").start();
    }


    public void calculateSpeed(){
        if(iAntiBotPlugin.getAntiBotManager().isSomeModeOnline()){
            this.speed = 100L;
        }else{
            this.speed = 1000L;
        }
    }

    public String getEmote() {
        return emote;
    }
}
