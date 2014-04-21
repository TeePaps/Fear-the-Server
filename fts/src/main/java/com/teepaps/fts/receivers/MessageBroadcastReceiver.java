package com.teepaps.fts.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.teepaps.fts.services.RoutingTableTransferService;
import com.teepaps.fts.ui.WifiActivity;

/**
 * Created by ted on 4/19/14.
 */
public class MessageBroadcastReceiver extends BroadcastReceiver {

    /**
     * Activity to perform actions on
     */
    private WifiActivity activity;

    /**
     * @param activity activity associated with the receiver
     */
    public MessageBroadcastReceiver(WifiActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (RoutingTableTransferService.BROADCAST_NOTIFICATION.equals(action)) {
//            Message message = (RoutingTable) intent
//                    .getSerializableExtra(RoutingTableTransferService.EXTRA_MESSAGE);
//            MessageDataSource.newInstance(activity).addMessage(message);

        }
    }
}
