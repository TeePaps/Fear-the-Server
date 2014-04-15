package com.teepaps.fts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.teepaps.fts.database.models.DataModel;
import com.teepaps.fts.database.models.Message;
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
    private static final String TABLE_NAME      = "messages";

    /**
     * Text from message
     */
    private static final String KEY_TEXT        = "text";

    /**
     * Unique ID of source
     */
    private static final String KEY_SOURCE      = "source";

    /**
     * Unique ID of destination
     */
    private static final String KEY_DESTINATION = "destination";

    /**
     * Name of table
     */
    private static final String COLUMN_DEFS      =
            DatabaseHelper.KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_SOURCE + " TEXT, "
            + KEY_DESTINATION + " TEXT, "
            + KEY_TEXT + " TEXT";

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
     * Create a new 'Message' entry in the database and return the new Peer created
     * @param source
     * @param destination
     * @return
     */
    public Message createMessage(String source, String destination, byte[] text) {
        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE, source);
        values.put(KEY_DESTINATION, destination);
        values.put(KEY_TEXT, text);

        return (Message) create(values);
    }

    /**
     * Retrieve a list of all the Peers in the database
     * @return
     */
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<Message>();

        Cursor cursor = database.query(TABLE_NAME,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while ((cursor != null) && !cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor);
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
    public Cursor getConversation(String source) {
        return database.query(TABLE_NAME, null, KEY_SOURCE + "=" + source, null, null, null, null);
    }

    /**
     * Wrapper to extract a 'Message' from a cursor
     * @param cursor
     */
    public Message cursorToMessage(Cursor cursor) {
        return (Message) cursorToModel(cursor);
    }

    /**
     * Extract a 'Message' from a cursor
     * @param cursor
     */
     @Override
    protected DataModel cursorToModel(Cursor cursor) {
        Message message = new Message();
        message.setRowId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROW_ID)));
        message.setSource(cursor.getString(cursor.getColumnIndex(KEY_TEXT)));
        message.setSource(cursor.getString(cursor.getColumnIndex(KEY_SOURCE)));
        message.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));

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
