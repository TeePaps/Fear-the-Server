package com.teepaps.fts.ui;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.teepaps.fts.utils.WiFiDirectBroadcastReceiver;

/**
 * Created by ted on 3/22/14.
 */
public class WifiActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(wifiManager, channel, this);
    }
}
