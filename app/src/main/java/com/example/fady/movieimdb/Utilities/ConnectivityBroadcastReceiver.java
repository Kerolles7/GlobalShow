package com.example.fady.movieimdb.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Fady on 2/3/2018.
 */

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    private ConnectivityReceiverListener mConnectivityReceiverListener;

    public ConnectivityBroadcastReceiver(ConnectivityReceiverListener mConnectivityReceiverListener) {
        this.mConnectivityReceiverListener = mConnectivityReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (mConnectivityReceiverListener != null && NetworkConnection.isConnected(context))
            mConnectivityReceiverListener.onNetworkConnectionConnected();

    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionConnected();
    }
}
