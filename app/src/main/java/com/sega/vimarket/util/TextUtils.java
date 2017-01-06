package com.sega.vimarket.util;

public class TextUtils {
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.equals("null") || str.equals(""));
    }
}
