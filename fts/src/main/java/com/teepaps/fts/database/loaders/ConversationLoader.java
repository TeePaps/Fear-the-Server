package com.teepaps.fts.database.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;

import com.teepaps.fts.database.MessageDataSource;

/**
 * Created by ted on 4/6/14.
 */
public class ConversationLoader extends CursorLoader {

    private static final String TAG = ConversationLoader.class.getSimpleName();

    /**
     * Context
     */
    private final Context context;

    /**
     * peerId
     */
    private final String peerId;

    private final String otherPeerId;

    public ConversationLoader(Context context, String peerId, String otherPeerId) {
        super(context);
        this.context        = context.getApplicationContext();
        this.peerId         = peerId;
        this.otherPeerId    = otherPeerId;
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, "Loading in background: peerId = " + String.valueOf(peerId)
                + " ***** other = " + String.valueOf(otherPeerId));
        return MessageDataSource.newInstance(context).getConversation(peerId, otherPeerId);
    }
}
