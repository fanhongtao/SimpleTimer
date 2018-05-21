package com.fht.simpletimer;

import java.util.Locale;

public class Utils {
    public static String formatTime(int hour, int minute, int second) {
        return String.format(Locale.getDefault(), Const.TIME_FORMAT, hour, minute, second);
    }
}
