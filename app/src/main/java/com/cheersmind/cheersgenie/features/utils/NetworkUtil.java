package com.cheersmind.cheersgenie.features.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络工具
 */
public class NetworkUtil {

    /**
     *
     * 描述：是否有网络连接.androidbase中AbWifiUtil中的方法
     * @param context 上下文
     * @return true：有网络
     */
    public static boolean isConnectivity(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                return ((connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                        .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || telephonyManager
                        .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 网络连接是否正常 * * @return true:有网络 false:无网络
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (mConnectivityManager != null) {
                    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (mNetworkInfo != null) {
                        return mNetworkInfo.isAvailable();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


}
