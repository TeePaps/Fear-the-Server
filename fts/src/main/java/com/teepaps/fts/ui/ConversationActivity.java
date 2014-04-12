package com.teepaps.fts.ui;

import android.app.Activity;
import android.os.Bundle;

public class ConversationActivity extends Activity
        implements ConversationFragment.ConversationFragmentListener
{
        private static final String TAG = ConversationActivity.class.getSimpleName();


    @Override
    public void setComposeText(String text) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

