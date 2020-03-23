package dev.shroysha.ap.readability.model;

public class ReadabilityWord {

    private static final char[] vowels = {'a', 'e', 'i', 'o', 'u', 'y'};
    private static final char[] allowed = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '\'', '-', '"', '(', ')',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private final int numOfSyl;
    private final String word;

    public ReadabilityWord(String word) {
        this.word = word;
        numOfSyl = countSyllables(word);
    }

    public static boolean isWord(String wordText) {

        wordText = wordText.toLowerCase();
        for (int i = 0; i < wordText.length(); i++) {
            boolean good = false;
            for (int j = 0; j < allowed.length && !good; j++) {
                if (wordText.charAt(i) == allowed[j]) {
                    good = true;
                    break;
                }
            }

            if (!good) {
                //System.out.println(wordText + " is not a word");
                return false;
            }
        }

        return true;

    }

    private int countSyllables(String word) {
        int syllables;

        word = word.toLowerCase().trim();

        int numOfVowels = countVowels(word);
        numOfVowels = removeConsecutiveVowels(word, numOfVowels);

        if (word.endsWith("e"))
            numOfVowels--;

        syllables = numOfVowels;

        if (syllables == 0) {
            syllables++;
        }

        return syllables;
    }

    public int getNumberOfSyllables() {
        return numOfSyl;
    }

    private int countVowels(String word) {
        int numVowels = 0;

        for (int i = 0; i < word.length(); i++) {
            for (char vowel : vowels) {
                if (word.charAt(i) == vowel) {
                    numVowels++;
                }
            }
        }

        return numVowels;
    }

    public String toString() {
        return word;
    }

    private int removeConsecutiveVowels(String word, int numOfVowels) {
        int consec = 0;

        for (int i = 0; i < word.length(); i++) {
            char charAt = word.charAt(i);
            for (char vowel : vowels) {
                //If the character is a vowel
                if (charAt == vowel) {
                    int k = 1;
                    for (; i + k < word.length() && isVowel(word.charAt(i + k)); k++) {
                        consec++;
                    }
                    i += k;
                }
            }
        }

        return numOfVowels - consec;
    }

    private boolean isVowel(char charAt) {

        for (char vowel : vowels) {
            if (charAt == vowel) {
                return true;
            }
        }

        return false;
    }

    public boolean isWord() {
        return ReadabilityWord.isWord(word);
    }
}
