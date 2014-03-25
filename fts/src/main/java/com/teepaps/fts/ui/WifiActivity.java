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
public class WifiActivity extends Activity
        implements WifiP2pManager.ActionListener
{

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
    IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(wifiManager, channel, this);

        // Create an intent filter and add the same intents that your broadcast receiver checks for
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int reasonCode) {

    }
}
