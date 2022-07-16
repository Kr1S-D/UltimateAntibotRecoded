package me.kr1s_d.ultimateantibot.common.objects;

public class FancyLong {
    private long value;

    public FancyLong() {
        value = 0;
    }

    public FancyLong(long initialValue) {
        this.value = initialValue;
    }

    public void add(long delta){
        value += delta;
    }

    public void remove(long delta){
        value -= delta;
    }

    public void remove(long delta, long min){
        value -= delta;

        if(value <= min){
            value = min;
        }
    }

    public void multiply(long delta){
        value *= delta;
    }

    public void divide(long delta){
        value /= delta;
    }

    public boolean between(long minValue, long maxValue) {
        return value >= minValue && value <= maxValue;
    }

    public long get() {
        return value;
    }
}
