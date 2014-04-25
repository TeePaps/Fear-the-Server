package com.teepaps.fts.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teepaps.fts.R;
import com.teepaps.fts.adapters.PeerListAdapter;
import com.teepaps.fts.database.loaders.PeerListLoader;
import com.teepaps.fts.database.models.Peer;
import com.teepaps.fts.utils.PeerConnectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows all the available Peers with some extra meta-data, such as:
 * {1} the number of hops to reach them,
 * {2} the type of connection at the final link,
 * {3} The route with the type of links,
 * {4} Whether the peer is in-range or not if already found.
 *
 * @author Created by ted on 4/13/14.
 */
public class PeerListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Peer>>, WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener
{
    /**
     * Devices found by wifiP2P
     */
    private List<WifiP2pDevice> deviceList = new ArrayList<WifiP2pDevice>();

    /**
     * Listener for when a conversation is selected.
     */
    private PeerSelectedListener listener;

    /**
     * Progress dialog long running tasks on this list
     */
    ProgressDialog progressDialog = null;

    /**
     * Util used for connecting to other peers. Using a fragment in case I want a UI.
     */
    private PeerConnectUtil connectUtil;

    private String peerId;
    private LayoutInflater inflater;
    private boolean isConnecting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.peer_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeListAdapter();
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (PeerSelectedListener) activity;
        this.isConnecting = false;
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewGroup header = (ViewGroup) inflater
                .inflate(R.layout.conversation_list_item, getListView(), false);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {

        isConnecting = true;
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                "Connecting...", true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
        );


        peerId = (String) view.getTag();
        ((PeerSelectedListener) getActivity()).onPeerSelected(peerId);
        Log.d("peer", "OnClick PeerId = " + String.valueOf(peerId));
    }

    /**
     * Reload the peer list on refresh or disconnect
     * @param wifiPeerList
     */
    public void reload(WifiP2pDeviceList wifiPeerList) {
        deviceList.clear();
        deviceList.addAll(wifiPeerList.getDeviceList());

        initializeListAdapter();
    }

    /**
     * Sets up the list adapter to list the conversations
     */
    private void initializeListAdapter() {
        setListAdapter(new PeerListAdapter(getActivity()));
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<List<Peer>> onCreateLoader(int id, Bundle args) {
        Log.w(MainActivity.TAG, "Creating the loader");
        if (deviceList != null) {
            Log.w(MainActivity.TAG, "device list size = " + deviceList.size());
        }
        return new PeerListLoader(getActivity(), deviceList);
    }

    @Override
    public void onLoadFinished(Loader<List<Peer>> listLoader, List<Peer> peers) {
        Log.w(MainActivity.TAG, "Finished loading");
        ((ArrayAdapter) getListAdapter()).addAll(peers);
    }

    @Override
    public void onLoaderReset(Loader<List<Peer>> listLoader) {
        Log.w(MainActivity.TAG, "Reset the loader");
        ((ArrayAdapter) getListAdapter()).clear();
//        ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        if (!isConnecting) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            reload(wifiP2pDeviceList);
        }

        if (wifiP2pDeviceList.getDeviceList().size() == 0) {
            Log.d(MainActivity.TAG, "No devices found");
            return;
        }
    }

    /**
     * Progress dialog to wait for peers
     *
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                }
        );
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Log.d("peer", "PeerId = " + String.valueOf(peerId));
        if (peerId == null) {
//           peerId = "awaiting MAC";
        }
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            Intent intent = new Intent(getActivity(), ConversationActivity.class);
            intent.putExtra(ConversationActivity.EXTRA_PEER_ID, peerId);
            getActivity().startActivity(intent);
        }
        else if (wifiP2pInfo.groupFormed) {
            Intent intent = new Intent(getActivity(), ConversationActivity.class);
            intent.putExtra(ConversationActivity.EXTRA_PEER_ID, peerId);
            intent.putExtra(ConversationActivity.EXTRA_HOST_ADDRESS,
                    wifiP2pInfo.groupOwnerAddress.getHostName());
            getActivity().startActivity(intent);
        }

    }

    public interface PeerSelectedListener {
        public void onPeerSelected(String peerId);
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

        void showDetails(WifiP2pDevice device);

        void cancelDisconnect();

        void connect(WifiP2pConfig config);

        void disconnect();
    }
}
