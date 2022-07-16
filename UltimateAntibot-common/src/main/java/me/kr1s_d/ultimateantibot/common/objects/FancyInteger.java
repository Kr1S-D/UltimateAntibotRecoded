package me.kr1s_d.ultimateantibot.common.objects;

public class FancyInteger {
    private int i;

    public FancyInteger(int i){
        this.i = i;
    }

    public int get() {
        return i;
    }

    public void increase(){
        i++;
    }

    public void decrease(){
        i--;
    }

    public void reset(){
        this.i = 0;
    }
}
