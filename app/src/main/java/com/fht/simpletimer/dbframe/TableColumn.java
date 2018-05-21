package com.fht.simpletimer.dbframe;

public class TableColumn {
    public String mName;
    public String mType;
    public boolean mNotNull;

    public TableColumn(String name, String type) {
        this(name, type, false);
    }

    public TableColumn(String name, String type, boolean notNull) {
        this.mName = name;
        this.mType = type;
        this.mNotNull = notNull;
    }
}
