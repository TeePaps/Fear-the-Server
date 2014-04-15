package com.teepaps.fts.database.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.teepaps.fts.database.PeerDataSource;

/**
 * Created by ted on 4/6/14.
 */
public class ConversationListLoader extends CursorLoader {

    /**
     * Context
     */
    private final Context context;

    public ConversationListLoader(Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public Cursor loadInBackground() {
//        return PeerDataSource.newInstance(context).getChattedPeers();
        return PeerDataSource.newInstance(context).getVisiblePeers();
    }
}
