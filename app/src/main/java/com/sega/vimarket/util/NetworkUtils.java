package com.sega.vimarket.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by Sega on 07/01/2017.
 */


public class NetworkUtils {

    private NetworkUtils() {}

    /**
     * Check network enable.
     * @param context application context.
     * @return if true network enabled.
     */
    public static boolean enableNetwork(Context context) {
        //        if (NetworkUtils.isAirplaneModeOn(context)) {
        //            return false;
        //        }
        return NetworkUtils.isConnected(context);
    }

    /**
     * Whether be able to network.
     *
     * @param context
     * @return Return true if network is enable.
     */
    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if( ni != null ){
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @param context
     * @return Return true if airplane mode.
     */
    public static boolean isAirplaneModeOn(Context context) {

        return Settings.System.getInt(context.getContentResolver(),
                                      Settings.System.AIRPLANE_MODE_ON, 0) != 0;

    }

}