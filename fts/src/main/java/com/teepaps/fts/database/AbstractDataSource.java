package com.teepaps.fts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teepaps.fts.database.models.DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 3/25/14.
 */
public abstract class AbstractDataSource {

    private static final String TAG = AbstractDataSource.class.getSimpleName();

    //******** NON-STATIC DATA MEMBERS ********
    //*****************************************

    /**
     * Database object
     */
    protected SQLiteDatabase database;

    /**
     * Helper for opening, creating, upgrading, etc.
     */
    protected DatabaseHelper dbHelper;

    /**
     * Create the table
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {
        String sqlStatement = "CREATE TABLE "
            + getTableName() + " (" + getColumnDefs() + ");";
        Log.d(TAG, sqlStatement);

        db.execSQL(sqlStatement);
    }

    /**
     * Upgrade table
     * @param db
     */
    public void onUpgrade(SQLiteDatabase db) {
        String sqlStatement = "DROP TABLE IF EXISTS " + getTableName();
        db.execSQL(sqlStatement);
    }

    //******** NON-STATIC METHODS ********
    //************************************

    /**
     * Default constructor
     */
    public AbstractDataSource() {
    }

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
     * Create a new 'DataModel' entry in the database.
     * @return the new 'DataModel' created.
     */
    public DataModel create(ContentValues contentValues) {
        DataModel newModel = null;

        open();
        long insertId = database.insert(getTableName(), null, contentValues);

        Cursor cursor = database.query(getTableName(),
                null, DatabaseHelper.KEY_ROW_ID + " = " + insertId, null,
                null, null, null);
        if (cursor.moveToFirst()) {
            newModel = cursorToModel(cursor);
            cursor.close();
        }

        return newModel;
    }

    /**
     * Delete a peer entry from the database
     * @param model
     */
    public void delete(DataModel model) {
        open();
        long id = model.getRowId();
        database.delete(getTableName(), DatabaseHelper.KEY_ROW_ID
                + " = " + id, null);
    }

    /**
     * Retrieve a list of all the DataModels in the database
     * @return
     */
    public List<DataModel> getAll() {
        open();
        List<DataModel> models = new ArrayList<DataModel>();

        Cursor cursor = database.query(getTableName(),
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

    //******** GETTERS ********
    //*************************

    /**
     * @return the static "TABLE_NAME" defined in the children
     */
    protected abstract String getTableName();

    /**
     * @return the static "TABLE_NAME" defined in the children
     */
    protected abstract String getColumnDefs();
}


