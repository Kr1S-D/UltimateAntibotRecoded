package me.kr1s_d.ultimateantibot.common.utils;

public class StringUtil {
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

    public static double calculateSimilarity(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        // check carattere per carattere
        int characterMatchCount = countCharacterMatches(str1, str2);
        int totalCharacters = Math.max(str1.length(), str2.length());

        // world checks
        int wordMatchCount = countWordMatches(str1, str2);
        int totalWords = Math.max(countWords(str1), countWords(str2));

        //conteggio delle lettere
        int letterMatchCount = countLetterMatches(str1, str2);
        int totalLetters = Math.max(countLetters(str1), countLetters(str2));

        double wordSimilarity = (double) wordMatchCount / totalWords;
        double letterSimilarity = (double) letterMatchCount / totalLetters;
        double characterSimilarity = (double) characterMatchCount / totalCharacters;

        // Calcolo della similarità totale come media dei tre rapporti
        double totalSimilarity = (characterSimilarity + wordSimilarity + letterSimilarity) / 3.0;
        return totalSimilarity * 100.0;
    }

    private static int countCharacterMatches(String str1, String str2) {
        int matchCount = 0;
        int minLength = Math.min(str1.length(), str2.length());
        for (int i = 0; i < minLength; i++) {
            if (Character.toLowerCase(str1.charAt(i)) == Character.toLowerCase(str2.charAt(i))) {
                matchCount++;
            }
        }
        return matchCount;
    }

    private static int countWordMatches(String str1, String str2) {
        String[] words1 = str1.split("\\s+");
        String[] words2 = str2.split("\\s+");
        int matchCount = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.equalsIgnoreCase(word2)) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

    private static int countWords(String str) {
        return str.split("\\s+").length;
    }

    private static int countLetterMatches(String str1, String str2) {
        int matchCount = 0;
        for (char c : str1.toCharArray()) {
            if (str2.indexOf(c) != -1) {
                matchCount++;
            }
        }
        return matchCount;
    }

    private static int countLetters(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                count++;
            }
        }
        return count;
    }
}
