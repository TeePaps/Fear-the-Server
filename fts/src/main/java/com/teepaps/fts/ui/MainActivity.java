package com.teepaps.fts.ui;

import android.app.Activity;
import android.os.Bundle;

import com.teepaps.fts.R;

/**
 * Created by ted on 3/22/14.
 */
//public class MainActivity extends WifiActivity
public class MainActivity extends Activity
        implements PeerListFragment.PeerSelectedListener,
                ConversationListFragment.ConversationSelectedListener
{
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

