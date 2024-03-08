package me.kr1s_d.ultimateantibot.common.objects;

public class PonderateEntry {
    private double value;
    private double weight;

    public PonderateEntry(double value, double weight) {
        this.value = value;
        this.weight = weight;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
