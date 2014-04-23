package com.teepaps.fts.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.teepaps.fts.MessageTransferFragment;
import com.teepaps.fts.R;
import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.database.models.FTSMessage;
import com.teepaps.fts.utils.AlertDialogFragment;
import com.teepaps.fts.utils.PrefsUtils;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

public class ConversationActivity extends RoboFragmentActivity
        implements ConversationFragment.ConversationFragmentListener,
        MessageTransferFragment.MessageActionListener
{
    private static final String TAG = ConversationActivity.class.getSimpleName();

    /**
     * Extra for peerId to pass to fragment
     */
    public static final String EXTRA_PEER_ID                = "peer_id";

    /**
     * Extra for peerId to pass to fragment
     */
    public static final String EXTRA_HOST_ADDRESS           = "host_address";

    /**
     * Tag for the MessageTransferFragment for this activity
     */
    private static final String TAG_FRAG_MESSAGE_TRANSFER   = "frag_msg_transfer";

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

    private String localMAC;

    /**
     * Host address of the group owner peer
     */
    private String hostAddress;

    /**
     * Manages the messaging service
     */
    private MessageTransferFragment messagingFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);

        peerId = getIntent().getStringExtra(EXTRA_PEER_ID);
        hostAddress = getIntent().getStringExtra(EXTRA_HOST_ADDRESS);

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
            messagingFragment = (MessageTransferFragment) getSupportFragmentManager()
                    .findFragmentByTag(TAG_FRAG_MESSAGE_TRANSFER);
        }
        else {
            messagingFragment = MessageTransferFragment.newInstance(peerId, hostAddress, 8888);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fl_messaging, messagingFragment, TAG_FRAG_MESSAGE_TRANSFER);
            transaction.commit();
        }

   }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        messagingFragment.sendFTSMessage(FTSMessage.newTerminateMessage());
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        AlertDialogFragment dialog = AlertDialogFragment
                .newInstance(android.R.drawable.ic_delete, R.string.alert_disconnect,
                        R.string.alert_disconnect_pos)
                .setOnClickListners(new AlertDialogFragment.AlertDialogOnClickListeners() {
                    @Override
                    public void doPositiveClick() {
                        messagingFragment.sendFTSMessage(FTSMessage.newTerminateMessage());
                    }

                    @Override
                    public void doNegativeClick() {
                        /* Do nothing */
                    }
                });
        dialog.show(getSupportFragmentManager(), "delete dialog");
    }

    @Override
    public void setComposeText(String text) {

    }

    @Override
    public void submitMessage(String text) {
        // Construct the message
        FTSMessage message = new FTSMessage(FTSMessage.TYPE_TEXT);
        message.setText(text);
        if (localMAC == null) {
            localMAC = PrefsUtils.getString(this, PrefsUtils.KEY_MAC, "Ted");
        }
        message.setSource(localMAC);
        message.setDestination(peerId);

        // Asynchronously sends message, onMessageReceived() callback handles the sent message
        messagingFragment.sendFTSMessage(message);
    }

    @Override
    public void onMessageReceived(FTSMessage message) {
        if (message.type == FTSMessage.TYPE_SENTINEL) {
            finish();
        }
        // Check for peerId info
        else if ((peerId == null) && (message.type == FTSMessage.TYPE_INFO)) {
            peerId = message.getSource();
            messagingFragment.setPeerId(peerId);
        }
        else {
            Log.d(TAG, "A message was received!");
            // Add the message and notify the fragment
            MessageDataSource.newInstance(this).addMessage(message);
            ConversationFragment fragment = (ConversationFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment_content);
            Log.d(TAG, "Reloading the message list");
            fragment.reload(peerId);
        }
    }
}

