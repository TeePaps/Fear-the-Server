/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teepaps.fts;

import android.app.Fragment;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.teepaps.fts.ui.ConversationActivity;
import com.teepaps.fts.ui.PeerListFragment;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class PeerConnectFragment extends Fragment implements ConnectionInfoListener {

    protected static final String ARG_DEVICE_ADDRESS = "device_address";

    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;

    private String deviceAddress;

    public static PeerConnectFragment newInstance(String deviceAddress) {
        PeerConnectFragment fragment = new PeerConnectFragment();
        Bundle args = new Bundle();

        args.putString(ARG_DEVICE_ADDRESS, deviceAddress);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            WifiP2pConfig config = new WifiP2pConfig();
            this.deviceAddress = args.getString(ARG_DEVICE_ADDRESS);
            config.deviceAddress = deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            ((PeerListFragment.DeviceActionListener) getActivity()).connect(config);
        }
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(ConversationActivity.EXTRA_PEER_ID, deviceAddress);
        startActivity(intent);
    }
}
