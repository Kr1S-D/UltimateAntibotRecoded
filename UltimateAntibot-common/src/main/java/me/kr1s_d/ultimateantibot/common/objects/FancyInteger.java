package me.kr1s_d.ultimateantibot.common.objects;

import java.io.Serializable;

public class FancyInteger implements Serializable {
    private int i;

    public FancyInteger(int i){
        this.i = i;
    }

    public int get() {
        return i;
    }

    public FancyInteger increase() {
        i++;
        return this;
    }

    public void decrease(){
        i--;
    }

    public void reset(){
        this.i = 0;
    }

    @Override
    public String toString() {
        return String.valueOf(i);
    }
}
