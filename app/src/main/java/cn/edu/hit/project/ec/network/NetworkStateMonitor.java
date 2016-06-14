package cn.edu.hit.project.ec.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateMonitor extends BroadcastReceiver {
    private static List<OnNetworkStateChangeListener> mListeners = new ArrayList<>();

    public interface OnNetworkStateChangeListener {
        void onConnect();
        void onDisconnect();
    }

    public NetworkStateMonitor() {}

    public static void registerListener(OnNetworkStateChangeListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public static void unregisterListener(OnNetworkStateChangeListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public static void checkNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            for (OnNetworkStateChangeListener listener : mListeners) {
                listener.onDisconnect();
            }
        } else {
            for (OnNetworkStateChangeListener listener : mListeners) {
                listener.onConnect();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkNetworkState(context);
    }
}
