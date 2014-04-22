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
    private String localMAC;

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
//        this.peerId = getActivity().getIntent().getStringExtra(
//                ConversationActivity.EXTRA_PEER_ID);
        this.peerId = "ae:22:0b:61:51:4f";
    }

    public void reload() {
       initializeListAdapter();
    }

    private void initializeListAdapter() {
        if (peerId != null) {
            setListAdapter(new ConversationAdapter(getActivity(), peerId, localMAC));
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ConversationLoader(getActivity(), peerId, localMAC);
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
        void setComposeText(String text);
        void submitMessage(String text);
    }
}
