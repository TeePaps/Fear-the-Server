package com.teepaps.fts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teepaps.fts.models.Peer;

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
     * Shared key for encryption
     */
    private static final String KEY_SHARED_KEY  = "shared_key";

    /**
     * Unique ID of Peer
     */
    private static final String KEY_PEER_ID     = "peer_id";

    /**
     * Is a connection to this peer open
     */
    private static final String KEY_IS_CONNECTED = "is_connected";

    /**
     * SQL statement to create table
     */
    private static final String COLUMN_DEFS    =
            DatabaseHelper.KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_SHARED_KEY + " TEXT "
            + KEY_PEER_ID + " TEXT "
            + KEY_IS_CONNECTED + " BOOLEAN NOT NULL CHECK (" + KEY_IS_CONNECTED + " IN (0,1))";

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

    //******** NON-STATIC METHODS ********
    //************************************

    /**
     * Constructor used to access database.
     * @param context
     */
    public PeerDataSource(Context context) {
        super(context);
    }

    /**
     * Static constructor
     * @param context
     */
    public static PeerDataSource newInstance(Context context) {
        return new PeerDataSource(context);
    }

    /**
     * Create a new Peer entry in the database and return the new Peer created
     *
     * @param peerId
     * @param sharedKey
     * @return
     */
    public Peer createPeer(long peerId, String sharedKey) {
        ContentValues values = new ContentValues();
        values.put(KEY_PEER_ID, peerId);
        values.put(KEY_SHARED_KEY, sharedKey);

        return (Peer) create(values);
    }

    /**
     * Retrieve a list of all the Peers in the database
     *
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
     *
     * @param cursor
     */
    @Override
    protected DataModel cursorToModel(Cursor cursor) {
        Peer peer = new Peer();
        peer.setRowId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROW_ID)));
        peer.setPeerId(cursor.getString(cursor.getColumnIndex(KEY_PEER_ID)));
        peer.setSharedKey(cursor.getString(cursor.getColumnIndex(KEY_SHARED_KEY)));
        peer.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_IS_CONNECTED)));

        return peer;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getColumnDefs() {
        return COLUMN_DEFS;
    }

}

