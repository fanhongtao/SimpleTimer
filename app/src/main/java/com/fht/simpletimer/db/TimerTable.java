package com.fht.simpletimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
        return columns;
    }

    @Override
    protected List<String> getCreateTableSQLs() {
        List<String> sqlList = super.getCreateTableSQLs();

        String sql = "CREATE INDEX idx_create_time on " + TABLE_NAME + " (" + COL_CREATE_TIME + ");";
        sqlList.add(sql);

        sql = "insert into " + TABLE_NAME +
                " (name, hour, minute, second, create_time)" +
                " values ('5 seconds', 0, 0, 5, " + System.currentTimeMillis() + ");";
        sqlList.add(sql);

        sql = "insert into " + TABLE_NAME +
                " (name, hour, minute, second, create_time)" +
                " values ('30 minutes (Siesta)', 0, 30, 0, " + System.currentTimeMillis() + ");";
        sqlList.add(sql);

        sql = "insert into " + TABLE_NAME +
                " (name, hour, minute, second, create_time)" +
                " values ('60 minutes (Bake bread)', 1, 0, 0, " + System.currentTimeMillis() + ");";
        sqlList.add(sql);
        return sqlList;
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
        return item;
    }

    public List<TimerItem> getTimerList() {
        return queryAllByOrder(COL_CREATE_TIME + " ASC");
    }

    public int updateTimer(TimerItem item) {
        ContentValues value = new ContentValues();
        value.put(COL_NAME, item.name);
        value.put(COL_HOUR, item.hour);
        value.put(COL_MINUTE, item.minute);
        value.put(COL_SECOND, item.second);
        return update(value, item.id);
    }
}
