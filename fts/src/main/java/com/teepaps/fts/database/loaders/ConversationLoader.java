package com.teepaps.fts.database.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.teepaps.fts.database.MessageDataSource;

/**
 * Created by ted on 4/6/14.
 */
public class ConversationLoader extends CursorLoader {

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
        return MessageDataSource.newInstance(context).getConversation(peerId, otherPeerId);
    }
}
