package com.teepaps.fts.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.teepaps.fts.PeerConnectUtil;
import com.teepaps.fts.R;
import com.teepaps.fts.database.PeerDataSource;
import com.teepaps.fts.receivers.WiFiDirectBroadcastReceiver;
import com.teepaps.fts.routing.RoutingTable;

/**
 * Created by ted on 3/22/14.
 */
public class WifiActivity extends Activity
        implements WifiP2pManager.ChannelListener, PeerListFragment.DeviceActionListener,
        PeerListFragment.PeerSelectedListener, ConversationListFragment.ConversationSelectedListener
{

    public static final String TAG = "WifiActivity";

    /**
     * Wifi manager to manage P2P connections
     */
    private WifiP2pManager wifiManager;

    /**
     * Connect app to wifi p2p framework
     */
    private WifiP2pManager.Channel channel;

    /**
     * Receiver for wifi signal
     */
    private BroadcastReceiver wifiBroadcastReceiver;

    /**
     * Receiver for sharing routing tables
     */
//    private RoutingTableBroadcastReceiver routingTableBroadcastReceiver;

    /**
     * Filter for the intents for the wifiBroadcastReceiver
     */
    private IntentFilter wifiIntentFilter = new IntentFilter();

    /**
     * Filter for the intents for the routingTableBroadcastReceiver
     */
//    private IntentFilter routingTableIntentFilter = new IntentFilter();

    /**
     * Is Wifi P2P enbabled for this device?
     */
    private boolean isWifiP2pEnabled;

    /**
     * Should we retry the channel?
     */
    private boolean retryChannel;

    /**
     * The routing table for connecting to the peers
     */
    private RoutingTable routingTable;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    PeerConnectUtil connectUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // add necessary intent values to be matched.
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//        routingTableIntentFilter.addAction(RoutingTableTransferService.BROADCAST_NOTIFICATION);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);

        routingTable = new RoutingTable("127.0.0.1");
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        disconnect();
        wifiBroadcastReceiver = new WiFiDirectBroadcastReceiver(wifiManager, channel, this);
        registerReceiver(wifiBroadcastReceiver, wifiIntentFilter);

//        routingTableBroadcastReceiver = new RoutingTableBroadcastReceiver(this);
//        registerReceiver(routingTableBroadcastReceiver, routingTableIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiBroadcastReceiver);
//        unregisterReceiver(routingTableBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
                if ((wifiManager != null) && (channel != null)) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                return true;

            case R.id.atn_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(this, R.string.p2p_off_warning,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                final PeerListFragment fragment = (PeerListFragment) getFragmentManager()
                        .findFragmentById(R.id.fragment_peer_list);
                fragment.onInitiateDiscovery();
                wifiManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiActivity.this, "Discovery Failed : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (wifiManager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
//            resetData();
            retryChannel = true;
            wifiManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void cancelDisconnect() {

    }

    @Override
    public void connect(WifiP2pConfig config) {
        wifiManager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        wifiManager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
//                fragment.getView().setVisibility(View.GONE);
            }

        });
    }


    /**
     * Update the routing table and the PeerListFragment
     * @param peerRoutingTable
     */
//    @Override
    public void updatePeerList(RoutingTable peerRoutingTable) {
        if (peerRoutingTable != null) {
            routingTable.merge(peerRoutingTable);
        }
        PeerListFragment listFragment = (PeerListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_peer_list);
        if (listFragment != null) {
            Toast.makeText(this, "PeerListFragment exists!", Toast.LENGTH_LONG).show();
//            listFragment.reload();
        }
    }

    @Override
    public void onPeerSelected(String peerId) {
        String key = PeerDataSource.newInstance(this).getPeer(peerId).getSharedKeyEncoded();
        Log.d(TAG, "key = " + String.valueOf(key));
        if (key == null) {
//            startNFSHandshake();
            Intent intent = new Intent(this, NfcActivity.class);
            intent.putExtra(ConversationActivity.EXTRA_PEER_ID, peerId);
            startActivity(intent);
        } else {
//            PeerConnectFragment.newInstance(peerId);
            connectUtil = new PeerConnectUtil(this, peerId);
        }
    }

    @Override
    public void onConversationSelected(String peerId) {
        Intent intent = new Intent(this, ConversationViewActivity.class);
        intent.putExtra(ConversationActivity.EXTRA_PEER_ID, peerId);
        startActivity(intent);
    }

}
