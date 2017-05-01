package com.playgrounds.api.utils;

import java.util.HashMap;
import java.util.Map;


public class Utils {

    // Unused util for convert greek char to english char

    private static final Map<Character, Character> GREEK_TO_ENGLISH = new HashMap<>();
    static {
        GREEK_TO_ENGLISH.put('\u03B1', '\u0061'); // alpha to a
        GREEK_TO_ENGLISH.put('\u03B2', '\u0062'); // beta to b
        GREEK_TO_ENGLISH.put('\u03B3', '\u0067'); // gama to g
        GREEK_TO_ENGLISH.put('\u03B4', '\u0064'); // delta to d
        GREEK_TO_ENGLISH.put('\u03B5', '\u0065'); // epsilon to e
        GREEK_TO_ENGLISH.put('\u03B6', '\u007A'); // zita to z
        GREEK_TO_ENGLISH.put('\u03B7', '\u0069'); // ita to i
        GREEK_TO_ENGLISH.put('\u03B8', '\u0074'); // thita to t
        GREEK_TO_ENGLISH.put('\u03B9', '\u0069'); // giota to i
        GREEK_TO_ENGLISH.put('\u03BA', '\u006B'); // kapa to k
        GREEK_TO_ENGLISH.put('\u03BB', '\u006C'); // lambda to l
        GREEK_TO_ENGLISH.put('\u03BC', '\u006D'); // mi to m
        GREEK_TO_ENGLISH.put('\u03BD', '\u006E'); // ni to n
        GREEK_TO_ENGLISH.put('\u03BE', '\u006A'); // ksi to j
        GREEK_TO_ENGLISH.put('\u03BF', '\u006F'); // omikron to o
        GREEK_TO_ENGLISH.put('\u03C0', '\u0070'); // pi to p
        GREEK_TO_ENGLISH.put('\u03C1', '\u0072'); // ro to r
        GREEK_TO_ENGLISH.put('\u03C2', '\u0073'); // sigma teliko to s
        GREEK_TO_ENGLISH.put('\u03C3', '\u0073'); // sigma to s
        GREEK_TO_ENGLISH.put('\u03C4', '\u0074'); // taf to t
        GREEK_TO_ENGLISH.put('\u03C5', '\u0075'); // ipsilon to u
        GREEK_TO_ENGLISH.put('\u03C6', '\u0066'); // fi to f
        GREEK_TO_ENGLISH.put('\u03C7', '\u0078'); // xi to x
        GREEK_TO_ENGLISH.put('\u03C8', '\u0063'); // psi to c
        GREEK_TO_ENGLISH.put('\u03C9', '\u006F'); // omega to o
    }
    public static String greekToEnglishConverter(String greekWord) {
        boolean isTheta = false;
        char[] chars = greekWord.toLowerCase().toCharArray();
        String englishWord = "";

        for (int i=0; i < chars.length; i++) {
            if (chars[i] == '\u03B8') {
                isTheta = true;
            }
            Character repl = GREEK_TO_ENGLISH.get(chars[i]);
            if (repl != null) {
                englishWord += repl;
            }
            if (isTheta) {
                englishWord += '\u0068';
                isTheta = false;
            }
        }
        return englishWord;
    }
}
