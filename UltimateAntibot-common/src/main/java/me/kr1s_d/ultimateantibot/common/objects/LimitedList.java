package me.kr1s_d.ultimateantibot.common.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class LimitedList<E> implements Iterable<E>, Serializable {
    private final List<E> list;
    private final int limit;

    public LimitedList(List<E> list, int limit) {
        this.list = list;
        this.limit = limit;
    }

    public LimitedList(int limit) {
        this.list = new ArrayList<>();
        this.limit = limit;
    }

    public LimitedList<E> add(E element){
        list.add(0, element);

        if(limit()){
            list.remove(list.size() - 1);
        }

        return this;
    }

    public boolean contains(E element) {
        return list.contains(element);
    }

    public boolean matches(Predicate<E> element) {
        boolean result = false;

        for (E e : list) {
            if(result) break;
            result = element.test(e);
        }

        return result;
    }

    public void remove(E element){
        list.remove(element);
    }

    public int size(){
        return list.size();
    }

    public void removeIf(Predicate<E> filter){
        list.removeIf(filter);
    }

    public void clear(){
        list.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    private boolean limit(){
        return size() > limit;
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
