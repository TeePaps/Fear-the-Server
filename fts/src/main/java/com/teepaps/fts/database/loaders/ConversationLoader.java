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

    public ConversationLoader(Context context, String peerId) {
        super(context);
        this.context = context.getApplicationContext();
        this.peerId = peerId;
    }

    @Override
    public Cursor loadInBackground() {
        return MessageDataSource.newInstance(context).getConversation(peerId);
    }
}
