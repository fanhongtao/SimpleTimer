package com.fht.simpletimer;

public class TimerItem {

    // Name of timer
    public String name;

    // hours.
    public int hour;

    // minutes
    public int minute;

    // seconds
    public int second;

    public TimerItem(String name, int hour, int minute, int second) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(name).append("', ");
        sb.append(Utils.formatTime(hour, minute, second));
        return sb.toString();
    }
}
