package com.teepaps.fts.ui;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

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
    private WiFiDirectBroadcastReceiver receiver;

    /**
     * Same intents as broadcast receiver
     */
    private IntentFilter intentFilter;

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
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
//        receiver = new WiFiDirectBroadcastReceiver(wifiManager, channel, this);
//        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int i) {

    }
}
