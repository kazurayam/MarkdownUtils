package com.kazurayam.markdownutils;

public class IdentifierTranslator {

    public static String translate(String pandocId) {
        return underline2hyphen(pandocId);
    }

    /*
     * "_foo_bar" -> "foo-bar"
     */
    public static String underline2hyphen(String s) {
        if (s.startsWith("_")) {
            return s.substring(1).replaceAll("_", "-");
        } else {
            return s.replaceAll("_", "-");
        }
    }


}
