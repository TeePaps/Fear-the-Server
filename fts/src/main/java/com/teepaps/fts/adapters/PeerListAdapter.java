package com.teepaps.fts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.teepaps.fts.R;
import com.teepaps.fts.database.models.Peer;

/**
 * Shows all previous conversations with different peers
 * Created by ted on 4/13/14.
 */
public class PeerListAdapter extends ArrayAdapter<Peer> {

    private static final String TAG = PeerListAdapter.class.getSimpleName();

    /**
     * Inflater for the view
     */
    LayoutInflater inflater;


    public PeerListAdapter(Context context) {
        // Start with no content for loader
        super(context, 0);

        this.inflater   = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.peer_list_item, null);
        }

        return convertView;
    }
}
