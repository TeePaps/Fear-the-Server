package com.teepaps.fts.core;

import com.teepaps.fts.database.DataModel;

/**
 * Handles message passing to the user and from the user. Abstracts away the
 * lower layers to use the public peer methods as an API for passing the
 * messages.
 * <p>
 * Created by ted on 3/25/14.
 * </p>
 */
public class Peer extends DataModel {

    /**
     * Unique ID of the Peer so that the application knows to communicate with
     * the proper Peer.
     */
    private long peerId;

    /**
     * Shared key between the user and peer to encrypt/decrypt messages.
     */
    private String sharedKey;

    /**
     * Send a message to this peer.
     * @param message
     */
    public void send(CharSequence message) {

    }

    /**
     * Receive a message, if present, from this peer.
     * @return
     */
    public CharSequence receive() {
        String message = null;

        return message;
    }

    //******** SETTERS ********
    //*************************
    public void setPeerId(long peerId) {
        this.peerId = peerId;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }
}
