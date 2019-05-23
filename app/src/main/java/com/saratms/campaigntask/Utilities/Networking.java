package com.saratms.campaigntask.Utilities;


import android.content.Context;
import android.net.ConnectivityManager;

public class Networking {

    public static boolean isConnected(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
