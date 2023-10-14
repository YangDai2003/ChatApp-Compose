package com.yangdai.chatapp.core

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Utils {

    /**
     *  判断网络连接是否正常
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}