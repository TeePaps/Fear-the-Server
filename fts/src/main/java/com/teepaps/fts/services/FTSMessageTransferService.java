package com.teepaps.fts.services;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.teepaps.fts.database.models.FTSMessage;
import com.teepaps.fts.ui.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FTSMessageTransferService extends AbstractService {

    /**
     * Key for host name
     */
    public static final String EXTRA_HOST           = "host";

    /**
     * Key for port number
     */
	public static final String EXTRA_PORT           = "port";

    /**
     * Key for FTSMessage
     */
	public static final String EXTRA_FTS_MESSAGE    = "fts_message";

    /**
     * 'What' value if message contains host and port info
     */
	public static final int MSG_CONNECT             = 1;

    /**
     * 'What' value if message contains an FTSMessage
     */
	public static final int MSG_FTS_MESSAGE         = 2;

    private NotificationManager nm;

    /**
     * Hostname of the server the client should connect to
     */
    private String host;

    /**
     * Port the client should connect on and the server should bind
     */
    private int port = -1;

    /**
     * Current message either sent or waiting to be sent
     */
    private FTSMessage ftsMessage;

    /**
     * Socket from the server
     */
    protected ServerSocket serverSocket;

    /**
     * Socket for the server to accept a client
     */
    protected Socket client;

    /**
     * Socket for the client to connect to the server on
     */
    protected Socket clientToServerSocket;

    /**
     * Check if the service has been stopped/cancelled
     */
    private boolean isCancelled = false;

	@Override
	public void onStartService() {
        if (port != -1) {
            if (host == null) {
                ServerReaderThread readerThread = new ServerReaderThread();
                readerThread.start();
            }
            else {
                ClientReaderThread readerThread = new ClientReaderThread();
                readerThread.start();
            }
            WriterThread writerThread = new WriterThread();
            writerThread.run();
        }
	}

   @Override
    public void onStopService() {

        nm.cancel(getClass().getSimpleName().hashCode());
    }   

	@Override
	public void onReceiveMessage(Message msg) {
		// Get argument from message, take square root and respond right away
        Bundle bundle = msg.getData();
        if (bundle != null) {
            switch (msg.what) {
                case MSG_CONNECT:
                    host = bundle.getString(EXTRA_HOST);
                    port = bundle.getInt(EXTRA_PORT);
                    break;
                case MSG_FTS_MESSAGE:
                    ftsMessage = (FTSMessage) bundle.getSerializable(EXTRA_FTS_MESSAGE);
                    break;
                default:
                    break;
            }
        }
	}

    private class ClientReaderThread extends Thread {
        @Override
        public void run() {
            clientToServerSocket = new Socket();

            try {
                //Create a client socket with the host, port, and timeout information.
                clientToServerSocket.bind(null);
                clientToServerSocket.connect((new InetSocketAddress(host, port)), 500);
                readLoop(new ObjectInputStream(clientToServerSocket.getInputStream()));
            } catch (FileNotFoundException e) {
                //catch logic
            } catch (IOException e) {
                //catch logic
            }
            // Clean up any open sockets when done transferring or if an exception occurred.
            finally {
                if (clientToServerSocket != null) {
                    if (clientToServerSocket.isConnected()) {
                        try {
                            clientToServerSocket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
            }
        }
    }

    private class ServerReaderThread extends Thread {
        @Override
        public void run() {
            try {
                // Create a server socket and wait for client connections. This call blocks until a
                // connection is accepted from a client
                serverSocket = new ServerSocket(port);
                client = serverSocket.accept();

                // Read until SENTINEL is sent
                readLoop(new ObjectInputStream(client.getInputStream()));

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(MainActivity.TAG, e.getMessage());
                }
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
            }
        }
    }

    private class WriterThread extends Thread {
        @Override
        public void run() {
            OutputStream outputStream = null;
            try {
                if (host == null) {    // This is the server
                    // Simply write the routing table object to the output stream
                    outputStream = client.getOutputStream();
                }
                else {
                    outputStream = clientToServerSocket.getOutputStream();
                }
                ObjectOutputStream objectOutputStream= new ObjectOutputStream(outputStream);
                if (objectOutputStream != null) {
                    objectOutputStream.writeObject(ftsMessage);
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Continually reads FTSMessages from the inputStream until a 'SENTINEL' message is sent
     * @param inputStream
     */
    private void readLoop(ObjectInputStream inputStream) {
        while (!isCancelled) {
            try {
                FTSMessage message = (FTSMessage) inputStream.readObject();
                if (message.type == FTSMessage.TYPE_SENTINEL) {
                    break;
                }
                publishResults(message);
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.w(MainActivity.TAG, "Something weird happened. Couldn't parse the input stream");
            }
        }

    }
    /**
     * Sends a message containing the FTSMessage to the fragment
     */
    private void publishResults(FTSMessage ftsMessage) {
        Message newMsg = Message.obtain(null, MSG_FTS_MESSAGE);

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FTS_MESSAGE, ftsMessage);
        newMsg.setData(bundle);

        send(newMsg);
    }
}