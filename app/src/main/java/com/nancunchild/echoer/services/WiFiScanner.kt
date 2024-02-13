package com.nancunchild.echoer.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import com.nancunchild.echoer.adapters.DeviceAdapter
import com.nancunchild.echoer.viewmodels.ScannerViewModel

class WiFiScanner(
    private val context: Context,
    private val viewModel: ScannerViewModel
) {
    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                Log.v("WiFiScanner","Scan Success In Receiver.")
            } else {
                // 在某些情况下，即使 EXTRA_RESULTS_UPDATED 为 false，scanResults 也可能包含数据
                Log.w("WiFiScanner","Scan Failed In Receiver.")
            }
            val results = wifiManager.scanResults
            val standardizedDeviceData = DeviceAdapter.fromWiFiScanResult(results)
            viewModel.updateWiFiScannedDevices(standardizedDeviceData)
            Log.v("WiFiScanner",standardizedDeviceData.toString())
        }
    }

    fun startScanning() {
        stopScanning()
        val filter = IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        context.registerReceiver(receiver, filter)
        viewModel.clearWiFiScannedDevices() // 清除旧的扫描结果
        wifiManager.startScan() // 谷歌有病，这个删除线不要管他，没有平替方法
    }

    fun stopScanning() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            Log.e("WiFiScanner", "Receiver not registered", e)
        }
    }
}
