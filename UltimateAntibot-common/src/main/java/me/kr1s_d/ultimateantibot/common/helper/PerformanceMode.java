package me.kr1s_d.ultimateantibot.common.helper;

public enum PerformanceMode {
    LOW(false, 7),
    AVERAGE(true, 10),
    MAX(true, 15),
    CUSTOM(true, 15);

    private final boolean a;
    private final int b;

    PerformanceMode(boolean isLatencyThreadEnabled, int antiBotModeTrigger) {
        this.a = isLatencyThreadEnabled;
        this.b = antiBotModeTrigger;
    }

    public boolean isLatencyThreadEnabled() {
        return a;
    }

    public int getAntiBotModeTrigger() {
        return b;
    }
}
