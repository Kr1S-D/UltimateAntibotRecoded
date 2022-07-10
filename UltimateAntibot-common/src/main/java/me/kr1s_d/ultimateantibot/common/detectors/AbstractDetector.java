package me.kr1s_d.ultimateantibot.common.detectors;

import me.kr1s_d.ultimateantibot.common.service.DetectorService;

public abstract class AbstractDetector {

    public AbstractDetector() {
        DetectorService.register(this);
    }

    public abstract int getTickDelay();

    public abstract void tick();
}
