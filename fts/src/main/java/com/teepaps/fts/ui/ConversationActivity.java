package com.teepaps.fts.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.teepaps.fts.R;
import com.teepaps.fts.database.MessageDataSource;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class ConversationActivity extends RoboActivity
        implements ConversationFragment.ConversationFragmentListener
{
    private static final String TAG = ConversationActivity.class.getSimpleName();

    /**
     * Extra for peerId to pass to fragment
     */
    public static final String EXTRA_PEER_ID = "peer_id";

    /**
     * Send button, using RoboGuice
     */
    @InjectView(R.id.send_button)           ImageButton bSend;

    /**
     * Text editor, using RoboGuice
     */
    @InjectView(R.id.embedded_text_editor)  EditText etEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etEditor.getText().toString();
                submitMessage(text);
                etEditor.setText("");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setComposeText(String text) {

    }

    @Override
    public void submitMessage(String text) {
        MessageDataSource.newInstance(this).createMessage("me", "ae:22:0b:61:51:4f", text);
        ConversationFragment fragment = (ConversationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_content);
        fragment.reload();
    }
}

