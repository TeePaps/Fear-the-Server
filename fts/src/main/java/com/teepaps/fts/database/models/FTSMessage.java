package com.teepaps.fts.database.models;

import android.util.Log;

import com.google.common.io.BaseEncoding;

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
     * This type is sent for regular text messages
     */
    public static final int TYPE_TEXT       = 1;

    /**
     * Type of message being sent
     */
    public final int type;

    /**
     * Text from message
     */
    private String text;

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

    //******** GETTERS ********
    //*************************

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public long getSentTime() {
        return sentTime;
    }

    public String getCipherText() {
        return cipherText;
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

    public String getText() {
        return text;
    }

    //******** SETTERS ********
    //*************************

    public void setText(String text) {
        this.text = text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
