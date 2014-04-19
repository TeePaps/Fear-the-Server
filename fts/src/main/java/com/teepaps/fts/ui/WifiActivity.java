package com.teepaps.fts.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.teepaps.fts.R;
import com.teepaps.fts.receivers.RoutingTableBroadcastReceiver;
import com.teepaps.fts.utils.WiFiDirectBroadcastReceiver;

/**
 * Created by ted on 3/22/14.
 */
public class WifiActivity extends Activity implements WifiP2pManager.ActionListener {

    private static final String TAG = "WifiActivity";

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
    private WiFiDirectBroadcastReceiver wifiBroadcastReceiver;

    /**
     * Receiver for sharing routing tables
     */
    private RoutingTableBroadcastReceiver routingTableBroadcastReceiver;

    /**
     * Filter for the intents for the wifiBroadcastReceiver
     */
    private IntentFilter wifiIntentFilter = new IntentFilter();

    /**
     * Filter for the intents for the routingTableBroadcastReceiver
     */
    private IntentFilter routingTableIntentFilter = new IntentFilter();

    /**
     * Is Wifi P2P enbabled for this device?
     */
    private boolean isWifiP2pEnabled;

    /**
     * Should we retry the channel?
     */
    private boolean retryChannel;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);

        // add necessary intent values to be matched.
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        wifiBroadcastReceiver = new WiFiDirectBroadcastReceiver(wifiManager, channel, this);
        registerReceiver(wifiBroadcastReceiver, wifiIntentFilter);

        routingTableBroadcastReceiver = new RoutingTableBroadcastReceiver();
        registerReceiver(routingTableBroadcastReceiver, routingTableIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiBroadcastReceiver);
        unregisterReceiver(routingTableBroadcastReceiver);
    }

    @Override
    public void onSuccess() {

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
                    Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                        .findFragmentById(R.id.frag_list);
                fragment.onInitiateDiscovery();
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFailure(int i) {

    }
}
