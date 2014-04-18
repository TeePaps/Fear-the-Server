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

import com.teepaps.fts.R;
import com.teepaps.fts.adapters.ConversationAdapter;
import com.teepaps.fts.database.loaders.ConversationLoader;

public class ConversationFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    /**
     * Callback for this fragment
     */
    private ConversationFragmentListener listener;

    /**
     * ID of the peer to communicate with
     */
    private String peerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.conversation_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        initializeResources();
        initializeListAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ConversationFragmentListener) activity;
    }

    private void initializeResources() {
        this.peerId = getActivity().getIntent().getStringExtra(
                ConversationActivity.EXTRA_PEER_ID);
    }

    private void initializeListAdapter() {
        if (peerId != null) {
            setListAdapter(new ConversationAdapter(getActivity(), peerId));
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ConversationLoader(getActivity(), peerId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ((CursorAdapter) getListAdapter()).changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter) getListAdapter()).changeCursor(null);
    }


    public interface ConversationFragmentListener {
        public void setComposeText(String text);
    }
}