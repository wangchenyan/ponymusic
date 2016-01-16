package me.wcy.music.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * Created by wcy on 2016/1/16.
 */
public class NetworkUtils {

    public static boolean isActiveNetworkMobile(Context paramContext) {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (localConnectivityManager != null) {
            NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
            if (localNetworkInfo != null && localNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}
