package me.kr1s_d.commons.objects.interfaces;

import java.util.List;

public interface IConfiguration{

    public Object get(String path);

    public String getString(String path);

    public int getInt(String path);

    public short getShort(String path);

    public long getLong(String path);

    public double getDouble(String path);

    public boolean getBoolean(String path);


    public List<String> getStringList(String path);

}
