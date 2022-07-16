package me.kr1s_d.ultimateantibot.common.objects;

public class FancyPair<T, V> {
    private T elementA;
    private V elementB;

    public FancyPair(T elementA, V elementB) {
        this.elementA = elementA;
        this.elementB = elementB;
    }

    public T getElementA() {
        return elementA;
    }

    public void setElementA(T elementA) {
        this.elementA = elementA;
    }

    public V getElementB() {
        return elementB;
    }

    public void setElementB(V elementB) {
        this.elementB = elementB;
    }
}
