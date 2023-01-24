package me.kr1s_d.ultimateantibot.common.utils;

import java.io.*;
import java.util.Base64;

public class SerializeUtil {
    public static String serialize(Object o){
        if(o == null) return "";
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(o);
            so.flush();
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(String base64){
        try {
            byte[] b = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bit = new ByteArrayInputStream(b);
            ObjectInputStream b1 = new ObjectInputStream(bit);
            return b1.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(String base64, Class<T> i){
        Object o = deserialize(base64);
        return o == null ? null : i.cast(o);
    }
}
