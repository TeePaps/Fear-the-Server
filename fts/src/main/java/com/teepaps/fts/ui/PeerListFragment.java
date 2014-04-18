package com.teepaps.fts.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teepaps.fts.R;
import com.teepaps.fts.adapters.ConversationListAdapter;
import com.teepaps.fts.database.loaders.PeerListLoader;
import com.teepaps.fts.database.models.Peer;

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
        implements LoaderManager.LoaderCallbacks<List<Peer>>, WifiP2pManager.PeerListListener
{
    /**
     * Devices found by wifiP2P
     */
    private List<WifiP2pDevice> deviceList = new ArrayList<WifiP2pDevice>();

    /**
     * Listener for when a conversation is selected.
     */
    private PeerSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.peer_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeListAdapter();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (PeerSelectedListener) activity;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        String peerId = (String) view.getTag();
        if (listener != null) {
            listener.onPeerSelected(peerId);
        }
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
        setListAdapter(new ConversationListAdapter(getActivity()));
//        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Peer>> onCreateLoader(int id, Bundle args) {
        return new PeerListLoader(getActivity(), deviceList);
    }

    @Override
    public void onLoadFinished(Loader<List<Peer>> listLoader, List<Peer> peers) {
        ((ArrayAdapter) getListAdapter()).addAll(peers);
    }

    @Override
    public void onLoaderReset(Loader<List<Peer>> listLoader) {
        ((ArrayAdapter) getListAdapter()).clear();
        ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        reload(wifiP2pDeviceList);
    }

    public interface PeerSelectedListener {
        public void onPeerSelected(String peerId);
    }
}
