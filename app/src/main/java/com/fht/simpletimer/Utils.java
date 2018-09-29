package com.fht.simpletimer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String formatTime(int hour, int minute, int second) {
        return String.format(Locale.getDefault(), Const.TIME_FORMAT, hour, minute, second);
    }

    public static String formatDateTime(long mill) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(mill));
    }
}
