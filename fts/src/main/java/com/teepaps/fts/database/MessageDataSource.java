package com.teepaps.fts.database;

import android.database.sqlite.SQLiteDatabase;

import javax.sql.DataSource;

/**
 * Created by ted on 3/25/14.
 */
public class MessageDataSource extends AbstractDataSource {

    /**
     * Name of table
     */
    private static final String TABLE_NAME      = "messages";

    /**
     * Name of table
     */
    private static final String KEY_SOURCE      = "source";

    /**
     * Name of table
     */
    private static final String KEY_DESTINATION = "destination";

    /**
     * SQL statement to create table
     */
    private static final String TABLE_CREATE    = "CREATE TABLE "
            + TABLE_NAME + " ("
            + DatabaseHelper.KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_SOURCE + " TEXT "
            + KEY_DESTINATION + " DESTINATION);";

    /**
     * SQL statement to upgrade table
     */
    private static final String TABLE_UPGRADE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * Create the table
     * @param db
     */
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    /**
     * Upgrade table
     * @param db
     */
    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL(TABLE_UPGRADE);
    }}
