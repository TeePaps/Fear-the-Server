package com.teepaps.fts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.teepaps.fts.core.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 3/25/14.
 */
public class PeerDataSource extends AbstractDataSource {

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

    @Override
    protected DataModel create() {
        return null;
    }

    /**
     * Create a new Peer entry in the database and return the new Peer created
     * @param peerId
     * @param sharedKey
     * @return
     */
    public Peer createPeer(long peerId, String sharedKey) {
        ContentValues values = new ContentValues();
        values.put(KEY_PEER_ID, peerId);
        values.put(KEY_SHARED_KEY, sharedKey);
        long insertId = database.insert(TABLE_NAME, null, values);
        Cursor cursor = database.query(TABLE_NAME,
                null, DatabaseHelper.KEY_ROW_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Peer newPeer = cursorToPeer(cursor);
        cursor.close();
        return newPeer;
    }

    /**
     * Delete a peer entry from the database
     * @param peer
     */
    public void deletePeer(Peer peer) {
        long id = peer.getRowId();
        database.delete(TABLE_NAME, DatabaseHelper.KEY_ROW_ID
                + " = " + id, null);
    }

    /**
     * Retrieve a list of all the Peers in the database
     * @return
     */
    public List<Peer> getAllPeers() {
        List<Peer> peers = new ArrayList<Peer>();

        Cursor cursor = database.query(TABLE_NAME,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while ((cursor != null) && !cursor.isAfterLast()) {
            Peer peer = cursorToPeer(cursor);
            peers.add(peer);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return peers;
    }


    /**
     * Wrapper to extract a Peer from a cursor
     *
     * @param cursor
     */
    private Peer cursorToPeer(Cursor cursor) {
        return (Peer) cursorToModel(cursor);
    }

    /**
     * Wrapper to extract a Peer from a cursor
     * @param cursor
     */
     @Override
    protected DataModel cursorToModel(Cursor cursor) {
        Peer peer = new Peer();
        peer.setRowId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROW_ID)));
        peer.setPeerId(cursor.getLong(cursor.getColumnIndex(KEY_PEER_ID)));
        peer.setSharedKey(cursor.getString(cursor.getColumnIndex(KEY_SHARED_KEY)));

        return peer;
    }

}

