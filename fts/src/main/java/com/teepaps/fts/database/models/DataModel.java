package com.teepaps.fts.database.models;

/**
 * Created by ted on 3/25/14.
 */
public class DataModel {

    /**
     * Row id for SQLite
     */
    private long rowId;

    //******** GETTERS ********
    //*************************
    public long getRowId() {
        return rowId;
    }

    //******** SETTERS ********
    //*************************
    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
}
