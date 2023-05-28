package com.kazurayam.markdownutils;

import java.util.regex.Pattern;

public class ATXHeadingId {

    private static Pattern UPPERCASE = Pattern.compile("[A-Z]");

    public static String of(String content) {
        StringBuilder sb = new StringBuilder();
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String s = String.valueOf(chars[i]);
            if (s.equals(" ")) {
                sb.append("-");
            } else if (s.equals("&") || s.equals("(") || s.equals(")") ||
                    s.equals(".") || s.equals("/") || s.equals(":")) {
                ; // will remove it
            } else if (UPPERCASE.matcher(s).matches()) {
                sb.append(s.toLowerCase());
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }
}
