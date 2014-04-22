package com.teepaps.fts.utils;

import android.content.Context;
import android.util.Log;

import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.database.models.FTSMessage;
import com.teepaps.fts.database.models.Peer;
import com.teepaps.fts.ui.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ted on 3/26/14.
 */
public class CommunicationUtils {

    /**
     * Routes the message to the proper 'Peer' specified by the messages 'source' and 'destination'
     * data members.
     * @param message - message to pass.
     */
    public static void send(FTSMessage message) {
        Log.w("Com", message.getDestination());
    }

    /**
     * Wrapper for sending a message from the user to a peer
     * @param peer - Peer to send string message to
     * @param textToSend
     */
    public static void send(Context context, Peer peer, String textToSend) {
        // Get the data source to write to and retrieve the model from
        MessageDataSource dataSource = new MessageDataSource(context.getApplicationContext());
        FTSMessage message = dataSource.createMessage("user", peer.getPeerId(), textToSend);

        // Send the message along
        send(message);

    }

    /**
     * Copies bytes from the inputStream to the outputStream.
     * @param inputStream
     * @param out
     * @return
     */
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
}
