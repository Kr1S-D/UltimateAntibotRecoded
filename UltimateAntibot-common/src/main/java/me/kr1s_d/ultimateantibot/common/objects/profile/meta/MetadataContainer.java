package me.kr1s_d.ultimateantibot.common.objects.profile.meta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetadataContainer<K> implements Serializable, Iterable<K> {
    private Map<K, Object> dataMap = new ConcurrentHashMap<>();
    private boolean persistent;

    public MetadataContainer(boolean persistent) {
        this.persistent = persistent;
    }

    public Object get(K key) {
        return dataMap.get(key);
    }

    public <T> T get(K key, Class<T> clas) {
        return clas.cast(dataMap.get(key));
    }

    public <T> T getOrDefaultNoPut(K key, Class<T> clazz, T def) {
        return clazz.cast(dataMap.getOrDefault(key, def));
    }

    public <T> T getOrPutDefault(K key, Class<T> clazz, T def) {
        if (dataMap.get(key) == null) {
            dataMap.put(key, def);
            return def;
        }

        return clazz.cast(dataMap.get(key));
    }

    public void insert(K key, Object value) {
        dataMap.put(key, value);
    }

    public int size() {
        return dataMap.size();
    }

    public void incrementInt(K key, int def) {
        Integer d = getOrPutDefault(key, Integer.class, def);
        insert(key, ++d);
    }

    public void remove(K key) {
        dataMap.remove(key);
    }

    public Map<K, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<K, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public boolean hasKey(K key) {
        return dataMap.containsKey(key);
    }

    public void clear() {
        dataMap.clear();
    }

    public boolean isPersistent() {
        return persistent;
    }

    /**
     * @param persistent Should this metadata be saved on server restart?
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public Iterator<K> iterator() {
        return dataMap.keySet().iterator();
    }
}
