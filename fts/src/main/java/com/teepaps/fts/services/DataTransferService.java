// Copyright 2011 Google Inc. All Rights Reserved.

package com.teepaps.fts.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * A service that process each file and text transfer request i.e Intent by
 * opening a socket connection with the WiFi Direct Group Owner and writing
 * the file
 */
public class DataTransferService extends IntentService {

    /**
     * Timeout for socket connection in milliseconds
     */
    private static final int SOCKET_TIMEOUT             = 5000;

    /**
     * Intent action to send a file using this service.
     */
    public static final String ACTION_SEND_FILE         = "com.teepaps.fts.services.SEND_FILE";

    /**
     * Intent action to send encrypted text using this service.
     */
    public static final String ACTION_SEND_TEXT         = "com.teepaps.fts.services.SEND_TEXT";

    /**
     * Extras key for file url passed to intent.
     */
    public static final String EXTRAS_FILE_PATH         = "file_url";

    /**
     * Extras key for byte array passed to intent.
     */
    public static final String EXTRAS_TEXT_BYTES        = "text_bytes";

    /**
     * Address to connect the socket to.
     */
    public static final String EXTRAS_SOCKET_ADDRESS    = "sock_host";

    /**
     * Port to create the socket on.
     */
    public static final String EXTRAS_SOCKET_PORT       = "sock_port";


    /**
     * Constructor for children to give a name to the service.
     * @param name
     */
    public DataTransferService(String name) {
        super(name);
    }

    /**
     * Default constructor
     */
    public DataTransferService() {
        super("DataTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        String action  = intent.getAction();
        /*if (action.equals(ACTION_SEND_FILE) || action.equals(ACTION_SEND_TEXT)) {
            sendOverSocket(intent);
        }*/
    }


 /*   private void sendOverSocket(Intent intent) {

        String host = intent.getExtras().getString(EXTRAS_SOCKET_ADDRESS);
        Socket socket = new Socket();
        int port = intent.getExtras().getInt(EXTRAS_SOCKET_PORT);

        try {
            Log.d(PeerListActivity.TAG, "Opening client socket - ");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

            // Create an output stream from the input stream.
            Log.d(PeerListActivity.TAG, "Client socket - " + socket.isConnected());
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream= getInputStream(intent);
            PeerConnectFragment.copyFile(inputStream, outputStream);
            Log.d(PeerListActivity.TAG, "Client: Data written");
        } catch (IOException e) {
            Log.e(PeerListActivity.TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    */

    /**
     * Using the intent action, determines which input stream to retrieve.
     *
     * @param intent - intent to get the action and extras from.
     * @return an inputStream with the content, null otherwise
     */
    /*private InputStream getInputStream(Intent intent) {
        InputStream stream = null;

        // If sending file, parse the file into the input stream.
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            try {
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
                stream = contentResolver.openInputStream(Uri.parse(fileUri));
            } catch (FileNotFoundException e) {
                Log.d(PeerListActivity.TAG, e.toString());
            }
        }
        // If sending text, get an imput stream from the byte array
        else if (intent.getAction().equals(ACTION_SEND_TEXT)) {
            byte[] textBytes = intent.getExtras().getByteArray(EXTRAS_TEXT_BYTES);
            stream = new ByteArrayInputStream(textBytes);
        }

        return stream;
    }*/
}
