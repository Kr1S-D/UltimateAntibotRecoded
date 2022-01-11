package me.kr1s_d.ultimateantibot.common.utils;

public class StringUtil {

    private final String string;

    private StringUtil(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public int countSimilarChars(String anotherString){
        int i = 0;

        for(int j = 0; j < string.length(); j++){
            char c1 = string.charAt(j);
            char c2 = anotherString.charAt(j);
            if(c1 == c2){
                i++;
            }
        }
        return i;
    }

    public static StringUtil from(String s){
        return new StringUtil(s);
    }
}
