package com.teepaps.fts.utils;

import android.content.Context;
import android.util.Log;

import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.database.models.Message;
import com.teepaps.fts.database.models.Peer;

/**
 * Created by ted on 3/26/14.
 */
public class CommunicationUtils {

    /**
     * Routes the message to the proper 'Peer' specified by the messages 'source' and 'destination'
     * data members.
     * @param message - message to pass.
     */
    public static void send(Message message) {
        Log.w("Com", message.getDestination());
    }

    /**
     * Wrapper for sending a message from the user to a peer
     * @param peer - Peer to send string message to
     * @param textToSend
     */
    public static void send(Context context, Peer peer, byte[] textToSend) {
        // Get the data source to write to and retrieve the model from
        MessageDataSource dataSource = new MessageDataSource(context.getApplicationContext());
        Message message = dataSource.createMessage("user", peer.getPeerId(), textToSend);

        // Send the message along
        send(message);

    }
}
