package me.kr1s_d.ultimateantibot.common.utils;

public class StringUtil {

    private final String string;

    private StringUtil(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public static int similarChars(String word1, String word2) {
        int count = 0;
        int maxLength = Math.max(word1.length(), word2.length());

        for (int i = 0; i < maxLength; i++) {
            try {
                if (String.valueOf(word1.charAt(i)).equalsIgnoreCase(String.valueOf(word2.charAt(i)))) {
                    count++;
                }
            }catch (Exception e) {

            }
        }

        return count;
    }

    public static StringUtil from(String s){
        return new StringUtil(s);
    }
}
