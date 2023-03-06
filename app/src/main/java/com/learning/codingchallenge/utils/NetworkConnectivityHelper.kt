package com.learning.codingchallenge.utils

import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.NetworkCapabilities
import android.net.Network
import kotlin.jvm.Synchronized

class NetworkConnectivityHelper private constructor(context: Context) : NetworkCallback() {
    private val connectivityManager: ConnectivityManager
    private var callback: NetworkConnectivityCallback? = null

    init {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun registerCallback(callback: NetworkConnectivityCallback?) {
        this.callback = callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        if (callback != null) {
            callback!!.onNetworkAvailable()
        }
    }

    override fun onLost(network: Network) {
        if (callback != null) {
            callback!!.onNetworkLost()
        }
    }

    companion object {
        private var instance: NetworkConnectivityHelper? = null
        @Synchronized
        fun getInstance(context: Context): NetworkConnectivityHelper? {
            if (instance == null) {
                instance = NetworkConnectivityHelper(context.applicationContext)
            }
            return instance
        }
    }
}