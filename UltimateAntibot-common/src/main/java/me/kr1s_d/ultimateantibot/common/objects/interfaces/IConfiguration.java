package me.kr1s_d.ultimateantibot.common.objects.interfaces;

import java.util.List;
import java.util.Set;

public interface IConfiguration{

    Object get(String path);

    String getString(String path);

    int getInt(String path);

    short getShort(String path);

    long getLong(String path);

    double getDouble(String path);

    boolean getBoolean(String path);

    List<String> getStringList(String path);

    Set<String> getConfigurationSection(String path);

    void set(String path, Object value);

    void save();

}
