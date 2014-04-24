package com.teepaps.fts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.common.io.BaseEncoding;
import com.teepaps.fts.R;
import com.teepaps.fts.crypto.CryptoUtils;
import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.database.PeerDataSource;
import com.teepaps.fts.database.models.FTSMessage;
import com.teepaps.fts.database.models.Peer;
import com.teepaps.fts.utils.ConversionUtils;

/**
 * Created by ted on 4/4/14.
 */
public class ConversationAdapter extends CursorAdapter {

    private static final String TAG = ConversationAdapter.class.getSimpleName();

    /**
     * Context
     */
    private Context context;

    /**
     * Outgoing message type
     */
    public static final int MESSAGE_TYPE_OUTGOING   = 1;

    /**
     * Incoming message type
     */
    public static final int MESSAGE_TYPE_INCOMING   = 2;

    /**
     * The Peer this conversation is with
     */
    private Peer peer;

    private String otherPeerId;

    /**
     * Inflater for the view
     */
    LayoutInflater inflater;

    boolean isDecrypted = true;

    public ConversationAdapter(Context context, String peerId, String otherPeerId,
                               boolean isDecrypted)
    {
        super(context, null, 0);

        this.context    = context;
        this.inflater   = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.peer       = PeerDataSource.newInstance(context).getPeer(peerId);
        this.otherPeerId = otherPeerId;
        this.isDecrypted = isDecrypted;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;

//        int type = getItemViewType(cursor);
        int type = MESSAGE_TYPE_OUTGOING;

        switch (type) {
            case ConversationAdapter.MESSAGE_TYPE_OUTGOING:
                view = inflater.inflate(R.layout.conversation_item, parent, false);
                view.setBackgroundResource(R.color.background_conversation_item_sent);
            case ConversationAdapter.MESSAGE_TYPE_INCOMING:
                view = inflater.inflate(R.layout.conversation_item, parent, false);
                break;
            default: throw new IllegalArgumentException("unsupported item view type given to ConversationAdapter");
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the layout elements
        TextView tvSender = (TextView) view.findViewById(R.id.tvSender);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        // Extract the FTSMessage object from the cursor
        FTSMessage message = MessageDataSource.newInstance(context).cursorToMessage(cursor);

        // Set the TextViews using the FTSMessage object
        if (message.getSource().equals(peer.getPeerId())) {
            tvSender.setText(peer.getPeerName());
        }
        else {
            tvSender.setText("Me");
        }
        try {
            tvDate.setText(ConversionUtils.milliToString(message.getSentTime()));
            Log.d(TAG, "KEY = " + BaseEncoding.base64().encode(peer.getSharedKeyBytes()));
            Log.d(TAG, "Cipher Text = " + message.getCipherText());
            if (isDecrypted) {
                tvMessage.setText(CryptoUtils.decrypt(peer.getSharedKeyBytes(), message.getCipherText()));
            } else {
                tvMessage.setText(message.getCipherText());
            }
        } catch (IllegalArgumentException iae) {
            Log.w(TAG, "Unable to format date given");
            tvDate.setText("");
        } catch (Exception e) {
            Log.w(TAG, "FTSMessage decryption failed");
            tvMessage.setText("Bad encrypted message");
        }

//        int type = getItemViewType(cursor);
        int type = MESSAGE_TYPE_OUTGOING;

        switch (type) {
            case ConversationAdapter.MESSAGE_TYPE_OUTGOING:

            case ConversationAdapter.MESSAGE_TYPE_INCOMING:
                break;
            default:
                throw new IllegalArgumentException("unsupported item view type given to ConversationAdapter");
        }

    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Cursor cursor = (Cursor)getItem(position);
//        return getItemViewType(cursor);
//    }
//
//    /**
//     * Checks whether the message held by the cursor is outgoing or incoming and returns accordingly
//     * @param cursor
//     * @return
//     */
//    private int getItemViewType(Cursor cursor) {
//        if (MessageDataSource.isOutgoing(context, cursor)) {
//            return MESSAGE_TYPE_OUTGOING;
//        }
//        return MESSAGE_TYPE_INCOMING;
//    }
}

