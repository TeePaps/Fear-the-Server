package com.teepaps.fts.models;

import com.teepaps.fts.database.DataModel;

import java.text.SimpleDateFormat;

/**
 * Created by ted on 3/25/14.
 */
public class Message extends DataModel {

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

    //******** GETTERS ********
    //*************************

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getSentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
        String date = sdf.format(sentTime);
        return date;
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
