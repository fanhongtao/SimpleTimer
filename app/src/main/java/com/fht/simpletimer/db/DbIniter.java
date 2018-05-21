package com.fht.simpletimer.db;

import com.fht.simpletimer.dbframe.SQLiteManager;

public class DbIniter {
    public static void init() {
        SQLiteManager manager = SQLiteManager.getInstance();
        manager.init("timer.db", 1);
        manager.registerTable(new TimerTable(null));
    }
}
