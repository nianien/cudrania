
package com.cudrania.core.text;


/**
 * Checks whether a string or path matches a given wildcard pattern.
 * Possible patterns allow to match single characters ('?') or any count of
 * characters ('*'). Wildcard characters can be escaped (by an '\').
 * When matching path, deep tree wildcard also can be used ('**').
 * <p>
 * This method uses recursive matching, as in linux or windows. regexp works the same.
 * This method is very fast, comparing to similar implementations.
 */
public class Wildcard {

    /**
     * Checks whether a string matches a given wildcard pattern.
     *
     * @param pattern pattern to match
     * @param string  input string
     * @return <code>true</code> if string matches the pattern, otherwise <code>false</code>
     */
    public static boolean match(CharSequence pattern, CharSequence string) {
        if (string.equals(pattern)) {
            return true;
        }
        return match(pattern, string, 0, 0);
    }

    /**
     * Internal matching recursive function.
     */
    private static boolean match(CharSequence pattern, CharSequence string, int sNdx, int pNdx) {
        int pLen = pattern.length();
        if (pLen == 1) {
            if (pattern.charAt(0) == '*') {     // speed-up
                return true;
            }
        }
        int sLen = string.length();
        boolean nextIsNotWildcard = false;

        while (true) {

            // check if end of string and/or pattern occurred
            if ((sNdx >= sLen)) {        // end of string still may have pending '*' in pattern
                while ((pNdx < pLen) && (pattern.charAt(pNdx) == '*')) {
                    pNdx++;
                }
                return pNdx >= pLen;
            }
            if (pNdx >= pLen) {                    // end of pattern, but not end of the string
                return false;
            }
            char p = pattern.charAt(pNdx);        // pattern char

            // perform logic
            if (!nextIsNotWildcard) {

                if (p == '\\') {
                    pNdx++;
                    nextIsNotWildcard = true;
                    continue;
                }
                if (p == '?') {
                    sNdx++;
                    pNdx++;
                    continue;
                }
                if (p == '*') {
                    char pNext = 0;                        // next pattern char
                    if (pNdx + 1 < pLen) {
                        pNext = pattern.charAt(pNdx + 1);
                    }
                    if (pNext == '*') {                    // double '*' have the same effect as one '*'
                        pNdx++;
                        continue;
                    }
                    int i;
                    pNdx++;

                    // find recursively if there is any substring from the end of the
                    // line that matches the rest of the pattern !!!
                    for (i = string.length(); i >= sNdx; i--) {
                        if (match(pattern, string, i, pNdx)) {
                            return true;
                        }
                    }
                    return false;
                }
            } else {
                nextIsNotWildcard = false;
            }

            // check if pattern char and string char are equals
            if (p != string.charAt(sNdx)) {
                return false;
            }

            // everything matches for now, continue
            sNdx++;
            pNdx++;
        }
    }


    // ---------------------------------------------------------------- utilities

    /**
     * Matches string to at least one pattern.
     * Returns index of matched pattern, or <code>-1</code> otherwise.
     *
     * @see #match(CharSequence, CharSequence)
     */
    public static int matchOne(String[] patterns, String input) {
        for (int i = 0; i < patterns.length; i++) {
            if (match(patterns[i], input)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Matches path to at least one pattern.
     * Returns index of matched pattern or <code>-1</code> otherwise.
     *
     * @see #matchPath(String, String)
     */
    public static int matchPathOne(String[] patterns, String path) {
        for (int i = 0; i < patterns.length; i++) {
            if (matchPath(patterns[i], path)) {
                return i;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------- path

    protected static final String PATH_MATCH = "**";
    protected static final String PATH_SEPARATORS = "/\\";

    /**
     * Matches path against pattern using *, ? and ** wildcards.
     * Both path and the pattern are tokenized on path separators (both \ and /).
     * '**' represents deep tree wildcard, as in Ant.
     */
    public static boolean matchPath(String pattern, String path) {
        char[] delimiters = PATH_SEPARATORS.toCharArray();
        String[] pathElements = splitc(path, delimiters);
        String[] patternElements = splitc(pattern, delimiters);
        return matchTokens(patternElements, pathElements);
    }

    /**
     * Match tokenized string and pattern.
     */
    protected static boolean matchTokens(String[] patterns, String[] tokens) {
        int patNdxStart = 0;
        int patNdxEnd = patterns.length - 1;
        int tokNdxStart = 0;
        int tokNdxEnd = tokens.length - 1;

        while ((patNdxStart <= patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {    // find first **
            String patDir = patterns[patNdxStart];
            if (patDir.equals(PATH_MATCH)) {
                break;
            }
            if (!match(patDir, tokens[tokNdxStart])) {
                return false;
            }
            patNdxStart++;
            tokNdxStart++;
        }
        if (tokNdxStart > tokNdxEnd) {
            for (int i = patNdxStart; i <= patNdxEnd; i++) {    // string is finished
                if (!patterns[i].equals(PATH_MATCH)) {
                    return false;
                }
            }
            return true;
        }
        if (patNdxStart > patNdxEnd) {
            return false;    // string is not finished, but pattern is
        }

        while ((patNdxStart <= patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {    // to the last **
            String patDir = patterns[patNdxEnd];
            if (patDir.equals(PATH_MATCH)) {
                break;
            }
            if (!match(patDir, tokens[tokNdxEnd])) {
                return false;
            }
            patNdxEnd--;
            tokNdxEnd--;
        }
        if (tokNdxStart > tokNdxEnd) {
            for (int i = patNdxStart; i <= patNdxEnd; i++) {    // string is finished
                if (!patterns[i].equals(PATH_MATCH)) {
                    return false;
                }
            }
            return true;
        }

        while ((patNdxStart != patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {
            int patIdxTmp = -1;
            for (int i = patNdxStart + 1; i <= patNdxEnd; i++) {
                if (patterns[i].equals(PATH_MATCH)) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patNdxStart + 1) {
                patNdxStart++;    // skip **/** situation
                continue;
            }
            // find the pattern between padIdxStart & padIdxTmp in str between strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patNdxStart - 1);
            int strLength = (tokNdxEnd - tokNdxStart + 1);
            int ndx = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = patterns[patNdxStart + j + 1];
                    String subStr = tokens[tokNdxStart + i + j];
                    if (!match(subPat, subStr)) {
                        continue strLoop;
                    }
                }

                ndx = tokNdxStart + i;
                break;
            }

            if (ndx == -1) {
                return false;
            }

            patNdxStart = patIdxTmp;
            tokNdxStart = ndx + patLength;
        }

        for (int i = patNdxStart; i <= patNdxEnd; i++) {
            if (!patterns[i].equals(PATH_MATCH)) {
                return false;
            }
        }

        return true;
    }


    private static String[] splitc(String src, char[] delimiters) {
        if ((delimiters.length == 0) || (src.length() == 0)) {
            return new String[]{src};
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (equalsOne(srcc[0], delimiters)) {    // string starts with delimiter
            end[0] = 0;
            count++;
            s = findFirstDiff(srcc, 1, delimiters);
            if (s == -1) {                            // nothing after delimiters
                return new String[]{"", ""};
            }
            start[1] = s;                            // new start
        }
        while (true) {
            // find new end
            e = findFirstEqual(srcc, s, delimiters);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = findFirstDiff(srcc, e, delimiters);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }


    private static boolean equalsOne(char c, char[] match) {
        for (char aMatch : match) {
            if (c == aMatch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds index of the first character in given array the differs from the
     * given set of characters.
     *
     * @return index of matched character or -1
     */
    private static int findFirstDiff(char[] source, int index, char[] match) {
        for (int i = index; i < source.length; i++) {
            if (!equalsOne(source[i], match)) {
                return i;
            }
        }
        return -1;
    }

    private static int findFirstEqual(char[] source, int index, char[] match) {
        for (int i = index; i < source.length; i++) {
            if (equalsOne(source[i], match)) {
                return i;
            }
        }
        return -1;
    }
}
