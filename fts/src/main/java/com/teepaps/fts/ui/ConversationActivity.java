package com.teepaps.fts.ui;

import android.app.Activity;
import android.os.Bundle;

public class ConversationActivity extends Activity
        implements ConversationFragment.ConversationFragmentListener
{
    private static final String TAG = ConversationActivity.class.getSimpleName();

    /**
     * Extra for peerId to pass to fragment
     */
    public static final String EXTRA_PEER_ID = "peer_id";


    @Override
    public void setComposeText(String text)
    {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

