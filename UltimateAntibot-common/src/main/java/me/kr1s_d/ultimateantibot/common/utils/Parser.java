package me.kr1s_d.ultimateantibot.common.utils;

public class Parser {
    public static int[] toIntArray(String[] s){
        int[] array = new int[s.length];
        for(int i = 0; i < s.length ; i++){
            array[i] = Integer.parseInt(s[i]);
        }
        return array;
    }

    public static String[] toArray(String str, String splitter){
        return str.split(splitter);
    }
}
