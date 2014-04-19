package com.teepaps.fts.services;

import android.app.IntentService;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Service that waits for clients to connect. When they do, this host will send it's routing table
 * to the connected client.
 *
 * @author Created by ted on 4/18/14.
 */
public abstract class AbstractTransferService extends IntentService {

    /**
     * Key for host to connect to with the socket
     */
    protected static final String EXTRA_HOST              = "host";

    /**
     * Key for port to connect the socket on
     */
    protected static final String EXTRA_PORT              = "port";

    /**
     * Notification to send back to the activity that registered
     */
    protected static final String BROADCAST_NOTIFICATION  = "default_notification";

    /**
     * Type of transfer service
     */
    protected int type;

    /**
     * Host to transfer the routing table to
     */
    protected String host;

    /**
     * Port to connect the socket on
     */
    protected int port;
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
    protected Socket socket;

    public AbstractTransferService(String name) {
        super(name);
    }

    public AbstractTransferService() {
        super("AbstractTransferService");
    }

    /**
     * Read the RoutingTable object from the stream
     */
    protected abstract void doServerWork();
    /**
     * Write the RoutingTable object to the stream
     */
    protected abstract void doClientWork();

    /**
     * Send a broadcast to the activity that registered this receiver
     */
    protected abstract void publishResults();

}
