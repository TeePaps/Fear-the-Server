package com.teepaps.fts.database.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;

import com.teepaps.fts.database.PeerDataSource;
import com.teepaps.fts.database.models.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ted on 4/6/14.
 */
public class PeerListLoader extends AsyncTaskLoader<List<Peer>> {

    /**
     * Context
     */
    private final Context context;

    /**
     * Devices found by wifiP2P
     */
    List<WifiP2pDevice> deviceList;

    /**
     * Default constructor
     * @param context
     * @param deviceList
     */
    public PeerListLoader(Context context, List<WifiP2pDevice> deviceList) {
        super(context);
        this.context = context.getApplicationContext();
        this.deviceList = deviceList;
    }

    @Override
    public List<Peer> loadInBackground() {
        PeerDataSource dataSource = new PeerDataSource(context);

        // Add all the found devices to the database if not already present.
        // If present, then update.
        List<Peer> peers = new ArrayList<Peer>();
        for (WifiP2pDevice device : deviceList) {

            // Check if we have already seen this peer
            Peer peer = dataSource.getPeer(device.deviceAddress);

            // If not seen before, add a new peer
            if (peer == null) {
                p2pDeviceToPeer(dataSource, device);
            }

            peers.add(peer);
        }

        return peers;
    }

    /**
     * Create a Peer from a WifiP2pDevice object
     * @param dataSource
     * @param device
     * @return
     */
    public static Peer p2pDeviceToPeer(PeerDataSource dataSource, WifiP2pDevice device) {
        // Create an unkeyed peer in the database
        Peer peer = dataSource.createPeer(device.deviceName, device.deviceAddress, null);
        return peer;
    }
}
