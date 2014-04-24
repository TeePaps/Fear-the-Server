package com.teepaps.fts.database.models;

import android.content.Context;
import android.util.Log;

import com.google.common.io.BaseEncoding;
import com.teepaps.fts.utils.PrefsUtils;

import java.io.Serializable;

/**
 * Created by ted on 3/25/14.
 */
public class FTSMessage extends DataModel implements Serializable {

    private static final String TAG = FTSMessage.class.getSimpleName();

    /**
     * This type is sent to close a socket
     */
    public static final int TYPE_SENTINEL   = 0;

    /**
     * This type is sent on connection to share info, such as MAC and key
     */
    public static final int TYPE_INFO       = 1;

    /**
     * This type is sent for regular text messages
     */
    public static final int TYPE_TEXT       = 2;

    /**
     * Type of message being sent
     */
    public final int type;

    /**
     * Source identifier
     */
    private String source;

    /**
     * Destination identifier
     */
    private String destination;

    /**
     * Cipher text from message sent
     */
    private String cipherText;

    /**
     * Time the message was created, in epoch time
     */
    private long creationTime;

    /**
     * Time the message was sent, in epoch time
     */
    private long sentTime;

    /**
     * Time the message was received, in epoch time
     */
    private long receivedTime;

    /**
     * Was the message read?
     */
    private boolean wasRead;

    /**
     * Constructor to init the type of the message
     * @param type
     */
    public FTSMessage(int type) {
        this.type = type;
    }

    /**
     * A message intended to just communicate some information
     * @param context
     * @return
     */
    public static FTSMessage newInfoMessage(Context context) {
        FTSMessage message = new FTSMessage(TYPE_INFO);
        message.setSource(PrefsUtils.getString(context, PrefsUtils.KEY_MAC, null));
        return message;
    }

    /**
     * A message intended to just communicate some information
     * @return
     */
    public static FTSMessage newTerminateMessage() {
        FTSMessage message = new FTSMessage(TYPE_SENTINEL);
        return message;
    }

    //******** GETTERS ********
    //*************************

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getCipherText() {
        return cipherText;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getSentTime() {
        return sentTime;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    /**
     * Get a byte[] representation of the cipher text message
     * @return
     */
    public byte[] getCipherBytes() {
        byte[] cipherBytes = null;

        try {
            cipherBytes = BaseEncoding.base64().decode(getCipherText());
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Failed to decode cipher text string into bytes");
        }

        return cipherBytes;
    }

    //******** SETTERS ********
    //*************************

    public void setCipherText(String text) {
        this.cipherText = text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }
}
