package com.fht.simpletimer.dbframe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteManager {
    private String mDbName;
    private int mDbVersion;
    private ArrayList<Table> mTables = new ArrayList<>();

    public void init(String dbName, int dbVersion) {
        mDbName = dbName;
        mDbVersion = dbVersion;
        mTables.clear();
    }

    private static SQLiteManager instance = null;
    public static SQLiteManager getInstance() {
        if (instance == null) {
            synchronized (SQLiteManager.class) {
                if (instance == null) {
                    instance = new SQLiteManager();
                }
            }
        }
        return instance;
    }

    public void registerTable(Table table) {
        mTables.add(table);
    }

    public DBOpenHelper getOpenHelper(Context context) {
        return new DBOpenHelper(context, mDbName, null, mDbVersion);
    }

    protected class DBOpenHelper extends SQLiteOpenHelper {
        private Context mContext;
        public DBOpenHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (Table table : mTables) {
                List<String> sqlList = table.getCreateTableSQLs(mContext);
                for (String sql : sqlList) {
                    db.execSQL(sql);
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int _oldVersion,int _newVersion) {
            for (Table table : mTables) {
                db.execSQL("DROP TABLE IF EXISTS " + table.mTableName);
            }
            onCreate(db);
        }
    }
}
