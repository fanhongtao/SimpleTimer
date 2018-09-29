package com.fht.simpletimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.fht.simpletimer.R;
import com.fht.simpletimer.TimerItem;
import com.fht.simpletimer.dbframe.Table;
import com.fht.simpletimer.dbframe.TableColumn;

import java.util.ArrayList;
import java.util.List;

public class TimerTable extends Table<TimerItem> {
    private static final String TABLE_NAME = "timer";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_HOUR = "hour";
    private static final String COL_MINUTE = "minute";
    private static final String COL_SECOND = "second";
    private static final String COL_CREATE_TIME = "create_time";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_REMAIN_TIME = "remain_time";

    public TimerTable(Context context) {
        super(context, TABLE_NAME, COL_ID);
    }

    @Override
    protected List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn(COL_ID, "integer primary key"));
        columns.add(new TableColumn(COL_NAME, "text"));
        columns.add(new TableColumn(COL_HOUR, "integer"));
        columns.add(new TableColumn(COL_MINUTE, "integer"));
        columns.add(new TableColumn(COL_SECOND, "integer"));
        columns.add(new TableColumn(COL_CREATE_TIME, "integer"));
        columns.add(new TableColumn(COL_START_TIME, "integer default 0"));
        columns.add(new TableColumn(COL_REMAIN_TIME, "integer default 0"));
        return columns;
    }

    @Override
    protected List<String> getCreateTableSQLs(Context context) {
        List<String> sqlList = super.getCreateTableSQLs(context);

        String sql = "CREATE INDEX idx_create_time on " + TABLE_NAME + " (" + COL_CREATE_TIME + ");";
        sqlList.add(sql);

        sqlList.add(getInsertSql(context.getString(R.string.timer_name_5s), 0, 0, 5));
        sqlList.add(getInsertSql(context.getString(R.string.timer_name_1m), 0, 1, 0));
        sqlList.add(getInsertSql(context.getString(R.string.timer_name_3m), 0, 3, 0));
        sqlList.add(getInsertSql(context.getString(R.string.timer_name_10m), 0, 10, 0));
        sqlList.add(getInsertSql(context.getString(R.string.timer_name_30m), 0, 30, 0));
        sqlList.add(getInsertSql(context.getString(R.string.timer_name_40m), 0, 40, 0));
        sqlList.add(getInsertSql(context.getString(R.string.timer_name_60m), 1, 0, 0));

        return sqlList;
    }

    private String getInsertSql(String name, int hour, int minute, int second) {
        return "insert into " + TABLE_NAME +
                " (name, hour, minute, second, create_time) values " +
                " ('" + name + "', " + hour + ", " + minute + ", " + second + ", " + System.currentTimeMillis() + ");";
    }

    public long addTimer(TimerItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.name);
        values.put(COL_HOUR, item.hour);
        values.put(COL_MINUTE, item.minute);
        values.put(COL_SECOND, item.second);
        values.put(COL_CREATE_TIME, System.currentTimeMillis());
        return super.insert(values);
    }

    @Override
    protected TimerItem readCursor(Cursor cursor) {
        TimerItem item = new TimerItem();
        item.id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        item.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        item.hour = cursor.getInt(cursor.getColumnIndex(COL_HOUR));
        item.minute = cursor.getInt(cursor.getColumnIndex(COL_MINUTE));
        item.second = cursor.getInt(cursor.getColumnIndex(COL_SECOND));
        item.startTime = cursor.getLong(cursor.getColumnIndex(COL_START_TIME));
        item.remainTime = cursor.getLong(cursor.getColumnIndex(COL_REMAIN_TIME));
        return item;
    }

    public List<TimerItem> getTimerList() {
        return queryAllByOrder(COL_HOUR + " ASC, " + COL_MINUTE + " ASC, "
                + COL_SECOND + " ASC, " + COL_CREATE_TIME + " ASC");
    }

    public int updateTimer(TimerItem item) {
        ContentValues value = new ContentValues();
        value.put(COL_NAME, item.name);
        value.put(COL_HOUR, item.hour);
        value.put(COL_MINUTE, item.minute);
        value.put(COL_SECOND, item.second);
        value.put(COL_START_TIME, item.startTime);
        value.put(COL_REMAIN_TIME, item.remainTime);
        return update(value, item.id);
    }

    public int setStartTime(TimerItem item) {
        ContentValues value = new ContentValues();
        value.put(COL_START_TIME, item.startTime);
        return update(value, item.id);
    }

    public int setRemainTime(TimerItem item) {
        ContentValues value = new ContentValues();
        value.put(COL_REMAIN_TIME, item.remainTime);
        return update(value, item.id);
    }
}
