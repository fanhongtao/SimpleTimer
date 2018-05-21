package com.fht.simpletimer.dbframe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class Table<T> {
    private SQLiteManager.DBOpenHelper mHelper;
    protected String mTableName;
    protected String mKey;

    public Table(Context context, String tableName, String key) {
        mHelper = (context == null) ? null : SQLiteManager.getInstance().getOpenHelper(context);
        mTableName = tableName;
        mKey = key;
    }

    abstract protected  List<TableColumn> getColumns();

    protected List<String> getCreateTableSQLs() {
        List<String> sqls = new ArrayList<>();

        StringBuilder sb = new StringBuilder(256);
        sb.append("create table ").append(mTableName).append(" (");
        int i = 0;
        for (TableColumn column : getColumns()) {
            if (i != 0) {
                sb.append(", ");
            }
            i++;
            sb.append(column.mName).append(" ").append(column.mType);
            if (column.mNotNull) {
                sb.append(" not null");
            }
        }
        sb.append(");");

        sqls.add(sb.toString());

        return sqls;
    }

    protected SQLiteDatabase getReadableDatabase() {
        return mHelper.getReadableDatabase();
    }

    protected SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    protected long insert(ContentValues value) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            return db.insert(mTableName, null, value);
        } finally {
            db.close();
        }
    }

    protected int update(ContentValues values, int keyValue) {
        return update(values, String.valueOf(keyValue));
    }

    protected int update(ContentValues values, long keyValue) {
        return update(values, String.valueOf(keyValue));
    }

    protected int update(ContentValues values, String keyValue) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            return db.update(mTableName, values, mKey + "=?", new String[]{ keyValue });
        } finally {
            db.close();
        }
    }

    public int delete(int keyValue) {
        return delete(String.valueOf(keyValue));
    }

    public int delete(long keyValue) {
        return delete(String.valueOf(keyValue));
    }

    public int delete(String keyValue) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            return db.delete(mTableName, mKey + "=?", new String[]{ keyValue });
        } finally {
            db.close();
        }
    }

    public long deleteAll() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            return db.delete(mTableName, null, null);
        } finally {
            db.close();
        }
    }

    abstract protected T readCursor(Cursor cursor);

    public T query(int keyValue) {
        return query(String.valueOf(keyValue));
    }

    public T query(String keyValue) {
        SQLiteDatabase db = getReadableDatabase();
        T record = null;
        Cursor cursor = db.query(mTableName, null,
                mKey + "=?", new String[]{ keyValue },
                null, null, null);
        try {
            while (cursor.moveToNext()) {
                record = readCursor(cursor);
                break;
            }
            return record;
        } finally {
            cursor.close();
            db.close();
        }
    }

    public List<T> queryAll() {
        return queryAll(null, null, null, null, null, null);
    }

    public List<T> queryAllByOrder(String orderBy) {
        return queryAll(null, null, null, null, null, orderBy);
    }

    public List<T> queryAll(String[] columns, String selection,
                               String[] selectionArgs, String groupBy, String having,
                               String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, columns, selection, selectionArgs,
                groupBy, having, orderBy);
        try {
            ArrayList<T> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                T record = readCursor(cursor);
                list.add(record);
            }
            return list;
        } finally {
            cursor.close();
            db.close();
        }
    }
}
