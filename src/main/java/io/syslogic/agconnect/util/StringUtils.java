package io.syslogic.agconnect.util;

import org.jetbrains.annotations.NotNull;

/**
 * This is method `capitalize(String string)` from class `org.apache.commons.lang3.StringUtils`, simplified and with annotations added.
 * @see <a href="https://commons.apache.org/proper/commons-lang/apidocs/src-html/org/apache/commons/lang3/StringUtils.html">StringUtils</a>
 * @author Apache Software Foundation
 */
public class StringUtils {

    /**
     * It capitalizes a given string.
     * @param string the input string.
     * @return the capitalized string.
     */
    @NotNull
    public static String capitalize(@NotNull String string) {
        int strLen = string.length();
        if (strLen == 0) {return string;}
        int firstCodepoint = string.codePointAt(0);
        int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) {return string;}
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            int codepoint = string.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint; // copy the remaining ones
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }
}
