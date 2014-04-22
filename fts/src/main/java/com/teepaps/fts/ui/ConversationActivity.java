package com.teepaps.fts.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.teepaps.fts.MessageTransferFragment;
import com.teepaps.fts.R;
import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.database.models.FTSMessage;

import roboguice.inject.InjectView;

public class ConversationActivity extends Activity
        implements ConversationFragment.ConversationFragmentListener,
        MessageTransferFragment.MessageActionListener
{
    private static final String TAG = ConversationActivity.class.getSimpleName();

    /**
     * Extra for peerId to pass to fragment
     */
    public static final String EXTRA_PEER_ID = "peer_id";

    /**
     * Tag for the MessageTransferFragment for this activity
     */
    private static final String TAG_FRAG_MESSAGE_TRANSFER = "frag_msg_transfer";
    /**
     * Send button, using RoboGuice
     */
    @InjectView(R.id.send_button)
    ImageButton bSend;

    /**
     * Text editor, using RoboGuice
     */
    @InjectView(R.id.embedded_text_editor)
    EditText etEditor;

    /**
     * MAC address of the peer to connect to
     */
    private String peerId;

    /**
     * Manages the messaging service
     */
    private MessageTransferFragment messagingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);

        peerId = getIntent().getStringExtra(EXTRA_PEER_ID);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etEditor.getText().toString();
                submitMessage(text);
                etEditor.setText("");
            }
        });

        // Add the message transfer fragment
        if (savedInstanceState != null) {
            messagingFragment = (MessageTransferFragment) getFragmentManager()
                    .findFragmentByTag(TAG_FRAG_MESSAGE_TRANSFER);
        }
        else {
            messagingFragment = MessageTransferFragment.newInstance(peerId, 8888);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(messagingFragment, TAG_FRAG_MESSAGE_TRANSFER);
            transaction.commit();
        }
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
        // Construct the message
        FTSMessage message = new FTSMessage(FTSMessage.TYPE_TEXT);
        message.setText(text);
        message.setDestination(peerId);
        message.setSource("Ted");

        // Asynchronously sends message, onMessageReceived() callback handles the sent message
        messagingFragment.sendMessage(message);
    }

    @Override
    public void onMessageReceived(FTSMessage message) {
        // Add the message and notify the fragment
        MessageDataSource.newInstance(this).addMessage(message);
        ConversationFragment fragment = (ConversationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_content);
        fragment.reload();
    }
}

