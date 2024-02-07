package com.nancunchild.echoer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import com.nancunchild.echoer.viewmodels.WiFiStatusViewModel

class WiFiStatusMonitor(private val context: Context, private val viewModel: WiFiStatusViewModel) {
    private var wifiStateReceiver: BroadcastReceiver? = null
    private var wifiInfoReceiver: BroadcastReceiver? = null

    fun startMonitoring() {
        registerWifiStateReceiver()
        registerWifiInfoReceiver()
    }

    fun stopMonitoring() {
        unregisterWifiStateReceiver()
        unregisterWifiInfoReceiver()
    }

    private fun registerWifiStateReceiver() {
        wifiStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    val wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN
                    )
                    when (wifiState) {
                        WifiManager.WIFI_STATE_ENABLED -> viewModel.updateWifiState("ON")
                        WifiManager.WIFI_STATE_DISABLED -> viewModel.updateWifiState("OFF")
                        // 其他状态可以根据需要处理
                    }
                }
            }
        }

        try {
            val wifiFilter = IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            }
            context.registerReceiver(wifiStateReceiver, wifiFilter)
        } catch (e: Exception) {
            Log.e("WifiStatusMonitor", "Error registering wifi state receiver: ${e.message}")
        }
    }

    private fun registerWifiInfoReceiver() {
        wifiInfoReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

                if (networkCapabilities != null && networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                ) {
                    val wifiManager =
                        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo
                    if (wifiInfo != null && wifiInfo.networkId != -1) {
                        val ssid = wifiInfo.ssid?.trim('"') ?: "" // 去除SSID周围的引号
                        val bssid = wifiInfo.bssid ?: ""
                        Log.v("WiFiStatusMonitor", wifiInfo.toString())
                        viewModel.updateWifiInfo(ssid, bssid)
                    }
                }
            }
        }

        try {
            val wifiFilter = IntentFilter().apply {
                addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            }
            context.registerReceiver(wifiInfoReceiver, wifiFilter)
        } catch (e: Exception) {
            Log.e("WiFiStatusMonitor", "Error registering wifi info receiver: ${e.message}")
        }
    }

    private fun unregisterWifiStateReceiver() {
        try {
            wifiStateReceiver?.let { context.unregisterReceiver(it) }
        } catch (e: Exception) {
            Log.e("WifiStatusMonitor", "Error unregistering wifi state receiver: ${e.message}")
        }
    }

    private fun unregisterWifiInfoReceiver() {
        try {
            wifiInfoReceiver?.let { context.unregisterReceiver(it) }
        } catch (e: Exception) {
            Log.e("WifiStatusMonitor", "Error unregistering wifi info receiver: ${e.message}")
        }
    }
}
