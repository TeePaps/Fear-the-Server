package com.teepaps.fts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.teepaps.fts.database.PeerDataSource;
import com.teepaps.fts.database.models.Peer;

/**
 * Shows all previous conversations with different peers
 * Created by ted on 4/13/14.
 */
public class ConversationListAdapter extends CursorAdapter {

    private static final String TAG = ConversationListAdapter.class.getSimpleName();

    /**
     * Inflater for the view
     */
    LayoutInflater inflater;

    public ConversationListAdapter(Context context) {
        super(context, null, 0);

        this.inflater   = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        View view = inflater.inflate(R.layout.conversation_item, parent, false);
        View view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

        // This line probably doesn't need to be here.
        bindView(view, context, cursor);
        //^^^^^^^^^^^^^^^^^^^^^^^^^

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the layout elements
//        TextView tvSender = (TextView) view.findViewById(R.id.tvSender);
        TextView tvSender = (TextView) view.findViewById(android.R.id.text1);
        TextView tvNum = (TextView) view.findViewById(android.R.id.text2);
        tvNum.setText("1");
        if (cursor != null) {
            tvNum.setText(cursor.getPosition());
        }

        // Extract the Peer model from the cursor
        Peer peer = PeerDataSource.newInstance(context).cursorToPeer(cursor);

        // Set the TextViews using the Message object
        String peerId = peer.getPeerId();
        tvSender.setText(peerId);
        view.setTag(peerId);
    }
}
