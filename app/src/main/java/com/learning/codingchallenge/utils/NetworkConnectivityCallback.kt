package com.learning.codingchallenge.utils

interface NetworkConnectivityCallback {
    fun onNetworkAvailable()
    fun onNetworkLost()
}