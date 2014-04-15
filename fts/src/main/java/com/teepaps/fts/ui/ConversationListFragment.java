package com.teepaps.fts.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.teepaps.fts.R;
import com.teepaps.fts.adapters.ConversationListAdapter;
import com.teepaps.fts.database.loaders.ConversationListLoader;

/**
 * Created by ted on 4/13/14.
 */
public class ConversationListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>
{

    /**
     * Listener for when a conversation is selected.
     */
    ConversationSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.conversation_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeListAdapter();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ConversationSelectedListener) activity;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        String peerId = (String) view.getTag();
        if (listener != null) {
            listener.onConversationSelected(peerId);
        }
    }

    /**
     * Sets up the list adapter to list the conversations
     */
    private void initializeListAdapter() {
        setListAdapter(new ConversationListAdapter(getActivity()));
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ConversationListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ((CursorAdapter) getListAdapter()).changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter) getListAdapter()).changeCursor(null);
    }

    public interface ConversationSelectedListener {
        public void onConversationSelected(String peerId);
    }
}
