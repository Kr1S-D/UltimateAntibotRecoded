package me.kr1s_d.ultimateantibot.common.objects;

import java.util.*;

public class LimitedFlushingMap<K, V> extends LinkedHashMap<K, V> {

    private final int sizeLimit;

    public LimitedFlushingMap(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    @Override
    public V put(K key, V value) {
        if(size() >= sizeLimit) removeLastElement();
        return super.put(key, value);
    }

    private void removeLastElement() {
        if (isEmpty()) {
            return;
        }

        Iterator<Map.Entry<K, V>> iterator = entrySet().iterator();
        if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}
