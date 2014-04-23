package com.teepaps.fts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.teepaps.fts.database.models.DataModel;
import com.teepaps.fts.database.models.FTSMessage;
import com.teepaps.fts.utils.PrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 3/25/14.
 */
public class MessageDataSource extends AbstractDataSource {

    //******** STATIC DATA MEMBERS ********
    //*************************************

    /**
     * Name of table
     */
    private static final String TABLE_NAME          = "messages";

    /**
     * Text from message
     */
    private static final String KEY_TEXT            = "text";

    /**
     * Unique ID of source
     */
    private static final String KEY_SOURCE          = "source";

    /**
     * Unique ID of destination
     */
    private static final String KEY_DESTINATION     = "destination";

    /**
     * Time of message creation
     */
    private static final String KEY_CREATION_TIME   = "creation_time";

    /**
     * Time the message was sent
     */
    private static final String KEY_SENT_TIME       = "sent_time";

    /**
     * Time the message was received
     */
    private static final String KEY_RECEIVED_TIME   = "received_time";

    /**
     * Name of table
     */
    private static final String COLUMN_DEFS         =
            DatabaseHelper.KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_SOURCE + " TEXT, "
            + KEY_DESTINATION + " TEXT, "
            + KEY_TEXT + " TEXT, "
            + KEY_CREATION_TIME + " INTEGER, "
            + KEY_SENT_TIME + " INTEGER, "
            + KEY_RECEIVED_TIME + " INTEGER";

    //******** NON-STATIC METHODS ********
    //************************************

    /**
     * Constructor used to access database.
     * @param context
     */
    public MessageDataSource(Context context) {
        super(context);
    }

    /**
     * Static constructor
     * @param context
     */
    public static MessageDataSource newInstance(Context context) {
        return new MessageDataSource(context);
    }

    /**
     * Create a new 'FTSMessage' entry in the database and return the new Peer created
     * @param source
     * @param destination
     * @return
     */
    public FTSMessage createMessage(String source, String destination, String text) {
        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE, source);
        values.put(KEY_DESTINATION, destination);
        values.put(KEY_TEXT, text);
        values.put(KEY_CREATION_TIME, System.currentTimeMillis());

        return (FTSMessage) create(values);
    }

    /**
     * Add a FTSMessage object to the database
     * @param message
     */
    public void addMessage(FTSMessage message) {
        String source, destination, cipherText;
        long creationTime, sentTime, receivedTime;

        ContentValues values = new ContentValues();

        if ((source = message.getSource()) != null) {
            values.put(KEY_SOURCE, source);
        }
        if ((destination = message.getDestination()) != null) {
            values.put(KEY_DESTINATION, destination);
        }
        if ((cipherText = message.getCipherText()) != null) {
            values.put(KEY_TEXT, cipherText.toString());
        }
        if ((creationTime = message.getCreationTime()) > 0) {
            values.put(KEY_CREATION_TIME, creationTime);
        }
        if ((sentTime = message.getSentTime()) > 0) {
            values.put(KEY_SENT_TIME, sentTime);
        }
        if ((receivedTime = message.getReceivedTime()) > 0) {
            values.put(KEY_RECEIVED_TIME, receivedTime);
        }
        create(values);
    }

    /**
     * Retrieve a list of all the Peers in the database
     * @return
     */
    public List<FTSMessage> getAllMessages() {
        open();
        List<FTSMessage> messages = new ArrayList<FTSMessage>();

        Cursor cursor = database.query(TABLE_NAME,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while ((cursor != null) && !cursor.isAfterLast()) {
            FTSMessage message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }

    /**
     * Get all message from a specific source.
     * @param source
     * @return
     */
    public Cursor getConversation(String destination, String source) {
        if (destination == null) {
            destination = "";
        }
        if (source == null) {
            source = "";
        }
        open();
        Cursor cursor = database.query(TABLE_NAME, null,
                KEY_DESTINATION + " = ? OR " + KEY_SOURCE + " = ?",
                new String[] { destination, source }, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * Wrapper to extract a 'FTSMessage' from a cursor
     * @param cursor
     */
    public FTSMessage cursorToMessage(Cursor cursor) {
        return (FTSMessage) cursorToModel(cursor);
    }

    /**
     * Extract a 'FTSMessage' from a cursor
     * @param cursor
     */
     @Override
    protected DataModel cursorToModel(Cursor cursor) {
        FTSMessage message = new FTSMessage(FTSMessage.TYPE_TEXT);
        message.setRowId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROW_ID)));
        message.setText(cursor.getString(cursor.getColumnIndex(KEY_TEXT)));
        message.setSource(cursor.getString(cursor.getColumnIndex(KEY_SOURCE)));
        message.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));
        message.setCreationTime(cursor.getLong(cursor.getColumnIndex(KEY_CREATION_TIME)));
        message.setSentTime(cursor.getLong(cursor.getColumnIndex(KEY_SENT_TIME)));
        message.setReceivedTime(cursor.getLong(cursor.getColumnIndex(KEY_RECEIVED_TIME)));

        return message;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getColumnDefs() {
        return COLUMN_DEFS;
    }

    /**
     * Check if the message is outgoing using the cursor
     * @param context
     * @param cursor
     * @return
     */
    public static boolean isOutgoing(Context context, Cursor cursor) {
        if(cursor.moveToFirst()) {
            String source = cursor.getString(cursor.getColumnIndex(KEY_SOURCE));
            // If source is not the user
            if (!source.equals(PrefsUtils.getString(context, PrefsUtils.KEY_MAC, null))) {
                return true;
            }
        }

        return false;
    }
}
