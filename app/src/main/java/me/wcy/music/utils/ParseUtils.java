package me.wcy.music.utils;

/**
 * Created by wcy on 2017/7/8.
 */
public class ParseUtils {

    public static long parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
