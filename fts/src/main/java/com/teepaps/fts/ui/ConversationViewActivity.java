package com.teepaps.fts.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.teepaps.fts.R;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

public class ConversationViewActivity extends RoboFragmentActivity
        implements ConversationFragment.ConversationFragmentListener
{

    private static final String TAG = ConversationViewActivity.class.getSimpleName();

    private boolean isDecrypted = true;
    private String peerId;

    @InjectView(R.id.sw_decrypt)
    Switch swDecrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_view_activity);

        peerId = getIntent().getStringExtra(ConversationActivity.EXTRA_PEER_ID);

        swDecrypt.setChecked(true);
        swDecrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isDecrypted = b;

                ConversationFragment fragment = (ConversationFragment) getFragmentManager()
                        .findFragmentById(R.id.fragment_content);
                Log.d(TAG, "Reloading the message list");
                fragment.setDecrypted(b);
            }
        });
   }

    @Override
    public void setComposeText(String text) {

    }

    @Override
    public void submitMessage(String text) {

    }
}

