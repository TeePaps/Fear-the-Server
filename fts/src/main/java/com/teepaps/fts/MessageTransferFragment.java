package com.teepaps.fts;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 4/22/14.
 */
public class MessageTransferFragment extends Fragment {

    private static final String TAG = MessageTransferFragment.class.getSimpleName();

    public static final String ARG_HOST = "host";
    public static final String ARG_PORT = "port";

    /**
     * Hostname of the server the client should connect to
     */
    private String host;

    /**
     * Port the client should connect on and the server should bind
     */
    private int port = -1;

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

    /**
     * Hold the socket IO threads
     */
    private List<Thread> threadPool = new ArrayList<Thread>();

    /**
     * Static constructor to add arguments to the fragment easily.
     * @param host
     * @param port
     * @return
     */
    public static MessageTransferFragment newInstance(String host, int port) {
        MessageTransferFragment fragment = new MessageTransferFragment();
        Bundle args = new Bundle();

        args.putString(ARG_HOST, host);
        args.putInt(ARG_PORT, port);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            this.host = args.getString(ARG_HOST);
            this.port = args.getInt(ARG_PORT, -1);
        }
    }

    /**
     * Define the connection params
     * @param host
     * @param port
     */
    public void defineSocket(String host, int port) {
        this.host = host;
        this.port = port;

    }

    /**
     * Start all the socket IO threads
     */
    public void start() {
        if (port != -1) {
            if (host == null) {
                ServerReaderThread readerThread = new ServerReaderThread();
                readerThread.start();
                threadPool.add(readerThread);
            }
            else {
                ClientReaderThread readerThread = new ClientReaderThread();
                readerThread.start();
                threadPool.add(readerThread);
            }
       }
    }

    @Override
    public void onPause() {
        stop();
        super.onPause();
    }

    /**
     * Stop all the activity Threads and close the sockets
     */
    public void stop() {
        for (Thread thread : threadPool) {
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Log.w(TAG, "Exception interrupting thread");
                }
            }
        }
    }

    /**
     * Asynchronously sends a message to the connected socket.
     * Callback notifies the calling activity
     */
    public void sendMessage(FTSMessage message) {
        WriterTask task = new WriterTask();
        task.execute(message);
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

    private class WriterTask extends AsyncTask<FTSMessage, Void, FTSMessage> {

        @Override
        protected FTSMessage doInBackground(FTSMessage... ftsMessages) {
            OutputStream outputStream = null;
            FTSMessage message = ftsMessages[0];

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
                    objectOutputStream.writeObject(message);
                    objectOutputStream.close();

                    // Set message as sent
//                    message.setReceieved(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return message;
        }

        @Override
        protected void onPostExecute(FTSMessage message) {
            ((MessageActionListener) getActivity()).onMessageReceived(message);
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
                ((MessageActionListener) getActivity()).onMessageReceived(message);
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.w(MainActivity.TAG, "Something weird happened. Couldn't parse the input stream");
            }
        }

    }

    public interface MessageActionListener {
        void onMessageReceived(FTSMessage message);
    }
}

