package com.teepaps.fts.ui;

import android.os.Bundle;

import com.teepaps.fts.R;

import roboguice.activity.RoboActivity;

/**
 * Created by ted on 3/22/14.
 */
//public class MainActivity extends WifiActivity
public class MainActivity extends RoboActivity
        implements PeerListFragment.PeerSelectedListener,
                ConversationListFragment.ConversationSelectedListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }


    @Override
    public void onConversationSelected(String peerId) {

    }

    @Override
    public void onPeerSelected(String peerId) {

    }
}

