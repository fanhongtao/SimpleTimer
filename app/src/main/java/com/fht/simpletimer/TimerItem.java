package com.fht.simpletimer;

import java.io.Serializable;

public class TimerItem implements Serializable {

    public long id;

    // Name of timer
    public String name;

    // hours.
    public int hour;

    // minutes
    public int minute;

    // seconds
    public int second;

    // Start time in milliseconds
    public long startTime;

    // Remained time in milliseconds
    public long remainTime;

    // If the timer is running or not.
    public boolean running;

    public TimerItem() {
    }

    public void calcRemainTime(long currTime) {
        remainTime -=  (currTime - startTime);
        if (remainTime < 0) {
            remainTime = 0;
        }
    }

    public void resetRemainTime() {
        this.remainTime = (hour * 3600 + minute * 60 + second) * 1000;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ID: ").append(id);
        sb.append(", '").append(name).append("', ");
        sb.append(Utils.formatTime(hour, minute, second));
        sb.append(", startTime:").append(startTime);
        sb.append(", remainTime:").append(remainTime);
        return sb.toString();
    }
}
