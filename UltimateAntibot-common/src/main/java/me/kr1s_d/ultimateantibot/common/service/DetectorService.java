package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.detectors.AbstractDetector;
import me.kr1s_d.ultimateantibot.common.objects.FancyInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectorService {
    private static final List<AbstractDetector> DETECTORS = new ArrayList<>();
    private static final Map<AbstractDetector, FancyInteger> TICKDATA = new HashMap<>();

    public static void tickDetectors(){
        for(AbstractDetector detector : DETECTORS){
            int tickDelay = detector.getTickDelay();
            FancyInteger i = TICKDATA.getOrDefault(detector, new FancyInteger(0));
            i.increase();
            TICKDATA.put(detector, i);

            if(tickDelay >= i.get()){
                detector.tick();
                TICKDATA.remove(detector);
            }
        }
    }

    public static void register(AbstractDetector detector){
        DETECTORS.add(detector);
    }

    public static void unregister(AbstractDetector detector){
        DETECTORS.remove(detector);
    }
}
