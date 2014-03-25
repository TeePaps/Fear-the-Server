package com.teepaps.fts.core;

import com.teepaps.fts.database.DataModel;

/**
 * Created by ted on 3/25/14.
 */
public class Message extends DataModel {
    /**
     * Source identifier
     */
    private String source;

    /**
     * Destination identifier
     */
    private String destination;

    //******** SETTERS ********
    //*************************

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }
}
