package com.fht.simpletimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.fht.simpletimer.HistoryItem;
import com.fht.simpletimer.dbframe.Table;
import com.fht.simpletimer.dbframe.TableColumn;

import java.util.ArrayList;
import java.util.List;

public class HistoryTable extends Table<HistoryItem> {
    private static final String TABLE_NAME = "history";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_STOP_TIME = "stop_time";

    public HistoryTable(Context context) {
        super(context, TABLE_NAME, COL_ID);
    }

    @Override
    protected List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn(COL_ID, "integer primary key"));
        columns.add(new TableColumn(COL_NAME, "text"));
        columns.add(new TableColumn(COL_START_TIME, "integer"));
        columns.add(new TableColumn(COL_STOP_TIME, "integer"));
        return columns;
    }

    @Override
    protected List<String> getCreateTableSQLs(Context context) {
        List<String> sqlList = super.getCreateTableSQLs(context);

        String sql = "CREATE INDEX idx_his_stop_time on " + TABLE_NAME + " (" + COL_STOP_TIME + ");";
        sqlList.add(sql);

        return sqlList;
    }

    public long addHistory(HistoryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.name);
        values.put(COL_START_TIME, item.startTime);
        values.put(COL_STOP_TIME, item.stopTime);
        return super.insert(values);
    }

    @Override
    protected HistoryItem readCursor(Cursor cursor) {
        HistoryItem item = new HistoryItem();
        item.id = cursor.getLong(cursor.getColumnIndex(COL_ID));
        item.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        item.startTime = cursor.getLong(cursor.getColumnIndex(COL_START_TIME));
        item.stopTime = cursor.getLong(cursor.getColumnIndex(COL_STOP_TIME));
        return item;
    }

    public List<HistoryItem> getHistoryList() {
        return queryAllByOrder(COL_STOP_TIME + " DESC");
    }
}
