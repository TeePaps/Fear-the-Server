package com.teepaps.fts.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 3/25/14.
 */
public abstract class AbstractDataSource {

    //******** STATIC DATA MEMBERS ********
    //*************************************

    /**
     * Name of table
     */
    private static final String TABLE_NAME      = "peers";

    /**
     * Name of table
     */
    private static final String KEY_SHARED_KEY  = "shared_key";

    /**
     * Name of table
     */
    private static final String KEY_PEER_ID     = "peer_id";

    /**
     * SQL statement to create table
     */
    private static final String TABLE_CREATE    = "CREATE TABLE "
            + TABLE_NAME + " ("
            + DatabaseHelper.KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_SHARED_KEY + " TEXT "
            + KEY_PEER_ID + " INTEGER);";

    /**
     * SQL statement to upgrade table
     */
    private static final String TABLE_UPGRADE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //******** NON-STATIC DATA MEMBERS ********
    //*****************************************

    /**
     * Datebase object
     */
    private SQLiteDatabase database;

    /**
     * Helper for opening, creating, upgrading, etc.
     */
    private DatabaseHelper dbHelper;

    //******** STATIC METHODS ********
    //********************************

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
    }

    //******** NON-STATIC METHODS ********
    //************************************

    /**
     * Constructor that instaniates a dbHelper
     * @param context
     */
    public AbstractDataSource(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Open the database for read/write
     * @throws SQLException
     */
    public void open() throws SQLException {
        this.database = dbHelper.getWritableDatabase();
    }

    /**
     * Close the database.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Create a new DataModel entry in the database and return the new Peer created
     * @return
     */
    protected abstract DataModel create();

    /**
     * Delete a peer entry from the database
     * @param model
     */
    public void delete(DataModel model) {
        long id = model.getRowId();
        database.delete(TABLE_NAME, DatabaseHelper.KEY_ROW_ID
                + " = " + id, null);
    }

    /**
     * Retrieve a list of all the DataModels in the database
     * @return
     */
    public List<DataModel> getAll() {
        List<DataModel> models = new ArrayList<DataModel>();

        Cursor cursor = database.query(TABLE_NAME,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while ((cursor != null) && !cursor.isAfterLast()) {
            DataModel model = cursorToModel(cursor);
            models.add(model);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return models;
    }


    /**
     * Wrapper to extract a Peer from a cursor
     * @param cursor
     */
    protected abstract DataModel cursorToModel(Cursor cursor);
}


