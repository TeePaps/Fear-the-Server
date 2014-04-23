package com.teepaps.fts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teepaps.fts.database.models.FTSMessage;
import com.teepaps.fts.ui.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by ted on 4/22/14.
 */
public class MessageTransferFragment extends RoboFragment {

    private static final String TAG = MessageTransferFragment.class.getSimpleName();

    public static final String ARG_PEER_ID          = "peer_id";
    public static final String ARG_HOST             = "host";
    public static final String ARG_PORT             = "port";

    public static final int MSG_TERMINATE           = 1;
    public static final int MSG_UPDATE              = 2;
    public static final int MSG_NEW_FTS_MESSAGE     = 3;

    /**
     * MAC address of the peer
     */
    private String peerId;

    /**
     * IP Hostname of the server the client should connect to
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

    protected ObjectInputStream inputStream;
    protected ObjectOutputStream outputStream;
    /**
     * Check if the service has been stopped/cancelled
     */
    private boolean isCancelled = false;

    /**
     * Hold the socket IO threads
     */
    private List<Thread> threadPool = new ArrayList<Thread>();

    /**
     * Handler to send updates of notifications to the main thread
     */
    private Handler threadCallbackHandler;

    /**
     * The current FTSMessage being processed;
     */
    private FTSMessage ftsMessage;

    /**
     * Sends the messages along
     */
    private WriterTask writerWorker;

    @InjectView(R.id.tv_host)       TextView tvPeer;

    @InjectView(R.id.tv_port)       TextView tvPort;

    @InjectView(R.id.tv_connected)  TextView tvConnected;

    @InjectView(R.id.b_connect)     TextView bConnect;


    /**
     * Static constructor to add arguments to the fragment easily.
     * @param host
     * @param port
     * @return
     */
    public static MessageTransferFragment newInstance(String peerId, String host, int port) {
        MessageTransferFragment fragment = new MessageTransferFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PEER_ID, peerId);
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
            this.peerId = args.getString(ARG_PEER_ID);
            this.host = args.getString(ARG_HOST);
            this.port = args.getInt(ARG_PORT, -1);
        }


        // Setup the handler for when the other party terminates the connection
        threadCallbackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case MSG_TERMINATE:
                        Log.d(TAG, "Sentinel message was received");
                        ((MessageActionListener) getActivity())
                                .onMessageReceived(FTSMessage.newTerminateMessage());
                        break;
                    case MSG_UPDATE:
                        Log.d(TAG, "Update message was received");
                        ((MessageActionListener) getActivity())
                                .onMessageReceived(FTSMessage.newInfoMessage(getActivity()));
                        break;
                    case MSG_NEW_FTS_MESSAGE:
                        Log.d(TAG, "FTS message was received");
                        Log.d(TAG, "Message was null? " + String.valueOf(ftsMessage == null));
                        ((MessageActionListener) getActivity())
                                .onMessageReceived(ftsMessage);
//                        ftsMessage = null;
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messaging_connection_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        start();
        tvPeer.setText(peerId);
        tvPort.setText(String.valueOf(port));
        if ((clientToServerSocket != null) || (client != null)) {
            tvConnected.setText("Yes!");
        }

        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Connect button pressed. Should we try to connect?");
                 if ((clientToServerSocket != null) && !clientToServerSocket.isConnected()) {
                     Log.d(TAG, "Trying to connect the client to the server again");
                     ClientReaderThread readerThread = new ClientReaderThread();
                     readerThread.start();
                     threadPool.add(readerThread);
                 }
            }
        });
    }

    /**
     * Define the connection params
     * @param host
     * @param port
     */
    public void defineSocket(String peerId, String host, int port) {
        this.peerId = peerId;
        this.host   = host;
        this.port   = port;

    }

    /**
     * Start all the socket I/O threads
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
    public void onStart() {
        if (threadPool.size() == 0) {
            Log.d(TAG, "No threads started yet. Resuming threads");
            start();
        }
        super.onStart();
    }

    public void onStop() {
        stop();
//        sendFTSMessage(FTSMessage.newTerminateMessage());
        super.onStop();
    }


    /**
     * Stop all the activity Threads and close the sockets
     */
    public void stop() {
        isCancelled = true;

        try {
            if (inputStream != null) {
                Log.d(TAG, "Closing inputStream");
                inputStream.close();
            }
            if (outputStream != null) {
                Log.d(TAG, "Closing outputStream");
                outputStream.close();
            }
//            if (serverSocket != null) {
//                Log.d(TAG, "Closing serverSocket");
//                serverSocket.close();
//            }
//            if (client != null) {
//                Log.d(TAG, "Closing client");
//                client.close();
//            }
//            if (clientToServerSocket != null) {
//                Log.d(TAG, "Closing clientToServerSocket");
//                clientToServerSocket.close();
//            }
        } catch (IOException e) {
            Log.w(TAG, "Error on stop(): " +e.getMessage());
        }
    }

    /**
     * Asynchronously sends a message to the connected socket.
     * Callback notifies the calling activity
     */
    public void sendFTSMessage(FTSMessage message) {
        WriterTask task;

        if (host == null) {
            task = new WriterTask(WriterTask.TYPE_SERVER, client, outputStream);
        } else {
            task = new WriterTask(WriterTask.TYPE_CLIENT, clientToServerSocket,
                    outputStream);
        }
        task.execute(message);

//        if (writerWorker == null) {
//            if (client != null) {
//                writerWorker = new WriterTask(WriterTask.TYPE_SERVER, client, outputStream);
//            }
//            else if (clientToServerSocket != null) {
//                writerWorker = new WriterTask(WriterTask.TYPE_CLIENT, clientToServerSocket,
//                        outputStream);
//            }
//        }
//
//        writerWorker.execute(message);
    }

    /**
     * Send a notification to the UI to do something
     * @param what
     */
    private void sendMessageToUI(int what) {
        Message message = threadCallbackHandler.obtainMessage(what);
        threadCallbackHandler.sendMessage(message);
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
        tvPeer.setText(peerId);
    }

    private class ClientReaderThread extends Thread {
        @Override
        public void run() {
            clientToServerSocket = new Socket();

            try {
                //Create a client socket with the host, port, and timeout information.
                sleep(5000);
                clientToServerSocket.setReuseAddress(true);
                clientToServerSocket.bind(null);
                Log.d(TAG, "Connecting to the client server socket at: " + host + ", " + String.valueOf(port));
                clientToServerSocket.connect((new InetSocketAddress(host, port)), 500);
                Log.d(TAG, "Connection to server successful");

                outputStream = new ObjectOutputStream(clientToServerSocket.getOutputStream());
                sendFTSMessage(FTSMessage.newInfoMessage(getActivity()));
                inputStream = new ObjectInputStream(clientToServerSocket.getInputStream());
                Log.d(TAG, "Got the inputStream from the server.");
                readLoop();
            } catch (FileNotFoundException e) {
                Log.w(TAG, e.getMessage());
            } catch (IOException e) {
                Log.w(TAG, e.getMessage());
            } catch (InterruptedException e) {
                Log.w(TAG, e.getMessage());
            }
            // Clean up any open sockets when done transferring or if an exception occurred.
            finally {
                if (clientToServerSocket != null) {
                    if (clientToServerSocket.isConnected()) {
                        try {
                            Log.d(TAG, "Server reading done. Closing clientToServerSocket.");
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
                Log.e(MainActivity.TAG, "Waiting for a client to connect on port " + String.valueOf(port));
                client = serverSocket.accept();
                Log.e(MainActivity.TAG, "Successfully connected to a client");

                // Read until SENTINEL is sent
                Log.e(MainActivity.TAG, "Getting inputStream from client");
                inputStream = new ObjectInputStream(client.getInputStream());
                readLoop();
                serverSocket.close();
            } catch (IOException e) {
                Log.e(MainActivity.TAG, "IOException in ServerReaderThread: " + e.getMessage());
            } finally {
                if (client != null) {
                    if (client.isConnected()) {
                        try {
                            Log.d(TAG, "Server reading done. Closing client.");
                            client.close();
                            client = null;
                            Log.d(TAG, "Server reading done. Closing serverSocket.");
                            serverSocket.close();
                            serverSocket = null;
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
            }
        }
    }

    private class WriterTask extends AsyncTask<FTSMessage, Void, FTSMessage> {

        public static final int TYPE_CLIENT = 1;
        public static final int TYPE_SERVER = 2;

        private int type;
        private Socket socket;
        private ObjectOutputStream outputStream;

        private WriterTask(int type, Socket socket, ObjectOutputStream outputStream) {
            this.type = type;
            this.socket = socket;
            if (outputStream != null) {
                this.outputStream = outputStream;
            }
        }

        @Override
        protected FTSMessage doInBackground(FTSMessage... ftsMessages) {
            FTSMessage message = ftsMessages[0];
            if (message != null) {
                Log.d(TAG, "Sending message of type: " + message.type);
            }

            if (socket != null) {
                Log.d(TAG, "About to write to the open socket");
                try {
                    if (outputStream == null) {
                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                        MessageTransferFragment.this.outputStream = outputStream;
                    }

                    // Type used for logging
                    if (type == TYPE_SERVER) {
                        Log.d(TAG, "Got the outputStream to the client");
                    }
                    else {
                        Log.d(TAG, "Got the outputStream to the server");
                    }

                    Log.d(TAG, "About to write object to stream");
                    message.setSentTime(System.currentTimeMillis());
                    outputStream.writeObject(message);
                    outputStream.flush();

                } catch (IOException e) {
                    Log.d(TAG, "IOException: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            return message;
        }

        @Override
        protected void onPostExecute(FTSMessage message) {
            // This will either notify the activity of a valid message or to terminate
            if (getActivity() != null) {
                ((MessageActionListener) getActivity()).onMessageReceived(message);
            }
        }
    }


    /**
     * Continually reads FTSMessages from the inputStream until a 'SENTINEL' message is sent
     */
    private void readLoop() {
        FTSMessage message = null;

        Log.d(TAG, "Entering read loop.");
        while (!isCancelled) {
            try {
                Log.d(TAG, "Reading an object");
                message = (FTSMessage) inputStream.readObject();
                Log.d(TAG, "Object was read");
                if ((message == null) || (message.type == FTSMessage.TYPE_SENTINEL)) {
                    Log.d(TAG, "Message was either null or SENTINEL");
                    sendMessageToUI(MSG_TERMINATE);
                    break;
                }
                Log.d(TAG, "Got message of type: " + message.type);
                message.setReceivedTime(System.currentTimeMillis());
                ftsMessage = message;
                sendMessageToUI(MSG_NEW_FTS_MESSAGE);

            } catch (IOException e) {
                Log.e(MainActivity.TAG, "IOException in read loop: " + e.getMessage());
                break;
            } catch (ClassNotFoundException e) {
                Log.w(MainActivity.TAG, "Something weird happened. Couldn't parse the input stream");
            }
        }
    }

    public interface MessageActionListener {
        void onMessageReceived(FTSMessage message);
    }
}

