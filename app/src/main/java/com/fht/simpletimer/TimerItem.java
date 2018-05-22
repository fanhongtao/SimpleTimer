package com.fht.simpletimer;

import java.io.Serializable;

public class TimerItem implements Serializable {

    // ID of the timer. Also the ROWID ( or primary key ) of the TimerTable.
    // Integer.MAX_VALUE = 0x7fffffff, or 2,147,483,647.
    // There are 60 * 60 * 24 * 365 = 31,536,000 seconds each year.
    // 2,147,483,647 / 31,536,000 = 68.096.. years.
    // That means, even if someone add a new timer every second, it will cost him 68 years to add more than 2 billion timers.
    // So, integer is enough for a timer app.
    public int id;

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
