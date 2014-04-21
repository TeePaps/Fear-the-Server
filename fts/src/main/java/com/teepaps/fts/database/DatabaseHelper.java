package com.teepaps.fts.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ted on 3/25/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG             = "DatabaseHelper";

    /**
     * Name of database
     */
    private static final String DATABASE_NAME   = "communication.db";

    /**
     * Version of database. Increment every time column changes are made.
     */
    private static final int DATABASE_VERSION   = 4;

    /**
     * Column name for primary key.
     */
    public static final String KEY_ROW_ID       = "_id";

    /**
     * Context
     */
    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables using their static methods
        MessageDataSource.newInstance(context).onCreate(db);
        PeerDataSource.newInstance(context).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade all tables using their static methods
        MessageDataSource.newInstance(context).onUpgrade(db);
        PeerDataSource.newInstance(context).onUpgrade(db);
        onCreate(db);
    }
}
