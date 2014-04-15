package com.teepaps.fts.database.models;

import android.content.Context;

import com.google.common.io.BaseEncoding;
import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.utils.ConversionUtils;

/**
 * Handles message passing to the user and from the user. Abstracts away the
 * lower layers to use the public peer methods as an API for passing the
 * messages.
 * <p>
 * Created by ted on 3/25/14.
 * </p>
 */
public class Peer extends DataModel {

    //******** STATIC DATA MEMBERS ********
    //*************************************

    public static final int CONNECTION_WIFI         = 0x1;
    public static final int CONNECTION_BLUETOOTH    = 0x2;

    //******** NON-STATIC DATA MEMBERS ********
    //*****************************************

    /**
     * Unique ID of the Peer so that the application knows to communicate with
     * the proper Peer.
     */
    private String peerId;

    /**
     * Shared key between the user and peer to encrypt/decrypt messages.
     */
    private String sharedKey;

    /**
     * Is a connection to this 'Peer' open?
     */
    private boolean isConnected;

    /**
     * Has this 'Peer' sent or received messages previously?
     */
    private boolean hasChatted;

    /**
     * Send a message to this peer.
     * @param message
     */
    public void send(Context context, String message) {
        MessageDataSource dataSource = new MessageDataSource(context);
        dataSource.createMessage("user", peerId, new byte[]{});

    }

    /**
     * Receive a message, if present, from this peer.
     * @return
     */
    public String receive() {
        String message = null;

        return message;
    }

    //******** GETTERS ********
    //*************************

    public String getPeerId() {
        return peerId;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    /**
     * Decodes the base64 representation of the key
     * @return
     */
    public byte[] getSharedKeyBytes() {
        return BaseEncoding.base64().decode(getSharedKey());
    }

    //******** SETTERS ********
    //*************************

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public void setSharedKey(byte[] key) {
        BaseEncoding.base64().encode(key);
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setChatted(boolean hasChatted) {
        this.hasChatted = hasChatted;
    }

    /**
     * Allows to write to this model using SQLite's INTEGER value.
     * @param isConnected
     */
    public void setConnected(int isConnected) {
        setConnected(ConversionUtils.intToBoolean(isConnected));
    }

    /**
     * Allows to write to this model using SQLite's INTEGER value.
     * @param isConnected
     */
    public void setChatted(int isConnected) {
        setChatted(ConversionUtils.intToBoolean(isConnected));
    }
}
