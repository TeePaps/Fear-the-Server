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

import android.app.Activity;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.view.View;

import com.teepaps.fts.ui.PeerListFragment;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class PeerConnectUtil {

    protected static final String ARG_DEVICE_ADDRESS = "device_address";

    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;

    private Activity activity;

    private String deviceAddress;

    public PeerConnectUtil(Activity activity, String deviceAddress) {

        this.activity = activity;

        if (deviceAddress != null) {
            WifiP2pConfig config = new WifiP2pConfig();
            this.deviceAddress = deviceAddress;
            config.deviceAddress = deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            ((PeerListFragment.DeviceActionListener) activity).connect(config);
        }
    }
}
