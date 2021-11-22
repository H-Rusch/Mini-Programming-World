package util;

import javax.lang.model.SourceVersion;

public class Validation {

    /** Checks whether a string is a positive numeric value. */
    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether a string is a Java identifier. Based on:
     * https://stackoverflow.com/questions/13979172/how-to-check-if-the-class-name-is-valid/33585127
     */
    public static boolean isJavaIdentifier(String str) {
        return SourceVersion.isIdentifier(str) && !SourceVersion.isKeyword(str);
    }
}
