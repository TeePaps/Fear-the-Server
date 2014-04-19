package com.teepaps.fts.services;

import android.content.Intent;
import android.util.Log;

import com.teepaps.fts.database.models.Message;
import com.teepaps.fts.ui.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Service that either:<br>
 *      1) Server: waits for a client to connect, then continually waits for objects to be sent<br>
 *      2) Client: sends one message to the host and port passed to the intent.
 *
 * @author Created by ted on 4/18/14.
 */
public class MessageTransferService extends AbstractTransferService {

    /**
     * Action to receive the table
     */
    private static final String ACTION_RECEIVE_MESSAGE  = "com.teepaps.fts.RECEIVE_MESSAGE";

    /**
     * Action to send the table
     */
    private static final String ACTION_SEND_MESSAGE     = "com.teepaps.fts.SEND_MESSAGE";

    /**
     * Key for routing table object serializable extra
     */
    private static final String EXTRA_MESSAGE           = "message";

    /**
     * Notification to send back to the activity that registered
     */
    private static final String BROADCAST_NOTIFICATION  = "routing_table";

    /**
     * Is this service cancelled?
     */
    private boolean isCancelled;

    /**
     * Last message retrieved from the client
     */
    private Message message;

    /**
     * Constructor for service when given a name
     * @param name
     */
    public MessageTransferService(String name) {
        super(name);
    }

    /**
     * Default constructor
     */
    public MessageTransferService() {
        super("RoutingTableTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        host    = intent.getStringExtra(EXTRA_HOST);
        port    = intent.getIntExtra(EXTRA_PORT, 8888);
        message = (Message) intent.getSerializableExtra(EXTRA_MESSAGE);

        // Send or receive a Routing Table
        String action = intent.getAction();
        if (action.equals(ACTION_RECEIVE_MESSAGE)) {
            doServerWork();
        } else if (action.equals(ACTION_SEND_MESSAGE)) {
            doClientWork();
        }
    }

    /**
     * Sets up a connection to the server and a thread to continually read messages from the client
     * until the client disconnects.
     */
    @Override
    protected void doServerWork() {
        try {
            // Create a server socket and wait for client connections. This call blocks until a
            // connection is accepted from a client
            serverSocket = new ServerSocket(port);
            client = serverSocket.accept();

            Thread messageRetriever = new Thread() {
                public void run() {
                    while (client.isConnected() && !isCancelled) {
                        try {
                            ObjectInputStream inputStream = new ObjectInputStream(client
                                    .getInputStream());
                            Message message = (Message) inputStream.readObject();
                            publishResults();
                        } catch (IOException e) {
                            Log.e(MainActivity.TAG, e.getMessage());
                        } catch (ClassNotFoundException e) {
                            Log.w(MainActivity.TAG, "Something weird happened. Couldn't parse the input stream");
                        }
                    }
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.e(MainActivity.TAG, e.getMessage());
                    }
                }
            };
            messageRetriever.start();

        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }

    /**
     * Write the RoutingTable object to the stream
     */
    @Override
    protected void doClientWork() {

        Socket socket = new Socket();
        byte buf[]  = new byte[1024];

        try {
             //Create a client socket with the host, port, and timeout information.
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            // Simply write the routing table object to the output stream
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(message);

            outputStream.close();
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }
        // Clean up any open sockets when done transferring or if an exception occurred.
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
    }

    /**
     * Send a broadcast to the activity that registered this receiver
     */
    protected void publishResults() {
        Intent intent = new Intent(BROADCAST_NOTIFICATION);
        intent.putExtra(EXTRA_MESSAGE, message);
        sendBroadcast(intent);
    }

}
