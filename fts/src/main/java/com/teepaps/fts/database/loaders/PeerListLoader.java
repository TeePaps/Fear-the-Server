package com.teepaps.fts.database.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.teepaps.fts.database.models.Peer;

import java.util.List;

/**
 * @author Created by ted on 4/6/14.
 */
public class PeerListLoader extends AsyncTaskLoader<List<Peer>> {

    /**
     * Context
     */
    private final Context context;

    public PeerListLoader(Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public List<Peer> loadInBackground() {
        return null;
    }
}
