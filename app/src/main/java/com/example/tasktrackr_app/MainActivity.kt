package com.example.tasktrackr_app

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tasktrackr_app.utils.LocalImageStorage
import com.example.tasktrackr_app.utils.NetworkChangeReceiver

class MainActivity : ComponentActivity() {
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityManager = getSystemService(ConnectivityManager::class.java)
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                NetworkChangeReceiver.triggerSyncIfWifi(this@MainActivity)
            }
        }
        val request = NetworkRequest.Builder().build()
        connectivityManager?.registerNetworkCallback(request, networkCallback!!)

        LocalImageStorage.init(this)

        setContent {
            TaskTrackrApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
    }
}
