package com.teepaps.fts.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.teepaps.fts.routing.RoutingTable;
import com.teepaps.fts.services.RoutingTableTransferService;
import com.teepaps.fts.ui.MainActivity;

/**
 * Created by ted on 4/19/14.
 */
public class RoutingTableBroadcastReceiver extends BroadcastReceiver {

    /**
     * Activity to perform actions on
     */
    private MainActivity activity;

    /**
     * @param activity activity associated with the receiver
     */
    public RoutingTableBroadcastReceiver(MainActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (RoutingTableTransferService.BROADCAST_NOTIFICATION.equals(action)) {
            RoutingTable routingTable = (RoutingTable) intent
                    .getSerializableExtra(RoutingTableTransferService.EXTRA_ROUTING_TABLE);
            activity.updatePeerList(routingTable);

        }
    }
}
