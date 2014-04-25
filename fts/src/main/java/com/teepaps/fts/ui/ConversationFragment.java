package com.teepaps.fts.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.teepaps.fts.R;
import com.teepaps.fts.adapters.ConversationAdapter;
import com.teepaps.fts.database.loaders.ConversationLoader;
import com.teepaps.fts.utils.PrefsUtils;

public class ConversationFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = ConversationFragment.class.getSimpleName();

    /**
     * Callback for this fragment
     */
    private ConversationFragmentListener listener;

    /**
     * ID of the peer to communicate with
     */
    private String peerId;
    private String localMAC;

    private boolean isDecrypted = true;

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
        localMAC = PrefsUtils.getString(getActivity(), PrefsUtils.KEY_MAC, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        reload(peerId);
        super.onListItemClick(l, v, position, id);
    }

    private void initializeResources() {
        this.peerId = getActivity().getIntent().getStringExtra(
                ConversationActivity.EXTRA_PEER_ID);
    }

    public void reload(String peerId) {
        this.peerId = peerId;
        localMAC = PrefsUtils.getString(getActivity(), PrefsUtils.KEY_MAC, null);

        if (getListAdapter() != null) {
            Log.d(TAG, "Not null so init list adapter in reload()");
            getLoaderManager().restartLoader(0, null, this);
        } else {
            Log.d(TAG, "list adapter was null in reload()");
            initializeListAdapter();
        }
    }

    private void initializeListAdapter() {
        Log.d(TAG, "Entered initializeListAdapter");
        if (peerId != null) {
            Log.d(TAG, "Peer = "+ String.valueOf(peerId) + "\nlocalMAC = " + String.valueOf(localMAC));
            setListAdapter(new ConversationAdapter(getActivity(), peerId, localMAC, true));
            getLoaderManager().initLoader(0, null, this);
        }
    }

    public void setDecrypted(boolean isDecrypted) {
        if (this.isDecrypted != isDecrypted) {
            this.isDecrypted = isDecrypted;
            setListAdapter(new ConversationAdapter(getActivity(), peerId, localMAC, isDecrypted));
            reload(peerId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating the loader");
        return new ConversationLoader(getActivity(), peerId, localMAC);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Loader finished");
        ((CursorAdapter) getListAdapter()).changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Reset the loader");
        ((CursorAdapter) getListAdapter()).changeCursor(null);
    }


    public interface ConversationFragmentListener {
        void setComposeText(String text);
        void submitMessage(String text);
    }
}
