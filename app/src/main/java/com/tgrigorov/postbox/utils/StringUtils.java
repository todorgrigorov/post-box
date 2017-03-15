package com.tgrigorov.postbox.utils;


import android.text.Html;
import android.text.SpannedString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean nullOrEmpty(String text) {
        return text == null || (text.equals(""));
    }

    public static String toPascalCase(String text) {
        StringBuilder buffer = new StringBuilder();
        for (String s : text.split("_")) {
            buffer.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                buffer.append(s.substring(1, s.length()).toLowerCase());
            }
        }
        return buffer.toString();
    }

    public static String encodeUnsafe(String text) {
        return !nullOrEmpty(text) ? text.replace("\"", "&quot;").replaceAll("(?s)<!--.*?-->", "") : text;
}

    public static String decodeUnsafe(String text) {
        return !nullOrEmpty(text) ? text.replace("&quot;", "\"") : text;
    }

    public static boolean isValidEmail(String text) {
        String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
