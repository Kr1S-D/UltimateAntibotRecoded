package me.kr1s_d.ultimateantibot.common.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class LimitedList<E> implements Iterable<E> {
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

    public void add(E element){
        list.add(0, element);

        if(limit()){
            list.remove(list.size() - 1);
        }
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
}
