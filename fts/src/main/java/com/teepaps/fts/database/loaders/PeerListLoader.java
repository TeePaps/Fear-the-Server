package com.teepaps.fts.database.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.teepaps.fts.database.PeerDataSource;
import com.teepaps.fts.database.models.Peer;
import com.teepaps.fts.ui.MainActivity;

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

    // We hold a reference to the Loader’s data here.
    private List<Peer> peers;

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
//            Peer peer = dataSource.getPeer(device.deviceAddress.replace(":", "_"));
            Peer peer = dataSource.getPeer(device.deviceAddress);

            // If not seen before, add a new peer
            if (peer == null) {
                peer = p2pDeviceToPeer(dataSource, device);
                Log.w(MainActivity.TAG, "peer.getName() = " + String.valueOf(peer.getPeerId()));
            }

            Log.w(MainActivity.TAG, "device.deviceAddress = " + String.valueOf(device.deviceAddress));
            peers.add(peer);
        }
        Log.w(MainActivity.TAG, "peers.size() = " + peers.size());

        return peers;
    }


    /********************************************************/
    /** (2) Deliver the results to the registered listener **/
    /********************************************************/

    @Override
    public void deliverResult(List<Peer> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<Peer> oldData = peers;
        peers = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(peers);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader’s state-dependent behavior **/
    /*********************************************************/

    @Override
    protected void onStartLoading() {
        if (peers != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(peers);
        }

        if (takeContentChanged() || peers == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (peers != null) {
            releaseResources(peers);
            peers = null;
        }
    }

    @Override
    public void onCanceled(List<Peer> data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<Peer> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
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
        Log.d(MainActivity.TAG, "Peer created: " + peer.toString());
        return peer;
    }
}
