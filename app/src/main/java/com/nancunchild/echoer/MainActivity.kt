package com.nancunchild.echoer

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.bluetooth.BluetoothManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivity : ComponentActivity() {
    private val bluetoothViewModel: BluetoothViewModel by viewModels()
    private val wifiViewModel: WifiViewModel by viewModels()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothStateReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothViewModel.initBluetoothState(bluetoothAdapter)

        setupBluetoothStateReceiver()
        setupWifiInfoReceiver()
        setupWifiStateReceiver()
        setContent {
            val context = LocalContext.current
            ScreenLayout()
        }
    }

    private fun setupBluetoothStateReceiver() {
        bluetoothStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state =
                            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        when (state) {
                            BluetoothAdapter.STATE_OFF -> {/* Handle Bluetooth OFF */ Log.v(
                                "Bluetooth",
                                "Bluetooth OFF"
                            )
                                bluetoothViewModel.updateBluetoothState("OFF")
                            }

                            BluetoothAdapter.STATE_ON -> {/* Handle Bluetooth ON */ Log.v(
                                "Bluetooth",
                                "Bluetooth ON"
                            )
                                bluetoothViewModel.updateBluetoothState("ON")
                            }

                            BluetoothAdapter.STATE_TURNING_ON -> {
                                Log.v("Bluetooth", "Bluetooth Turning ON")
                                bluetoothViewModel.updateBluetoothState("Turning ON...")
                            }

                            BluetoothAdapter.STATE_TURNING_OFF -> {
                                Log.v("Bluetooth", "Bluetooth Turning OFF")
                                bluetoothViewModel.updateBluetoothState("Turning OFF...")
                            }

                            BluetoothAdapter.STATE_CONNECTED -> {
                                Log.v("Bluetooth", "Bluetooth Connected")
                            }

                            BluetoothAdapter.STATE_CONNECTING -> {
                                Log.v("Bluetooth", "Bluetooth Connecting")
                            }

                            BluetoothAdapter.STATE_DISCONNECTED -> {
                                Log.v("Bluetooth", "Bluetooth Disconnected")
                            }

                            BluetoothAdapter.STATE_DISCONNECTING -> {
                                Log.v("Bluetooth", "Bluetooth Disconnecting")
                            }
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)
    }

    private fun setupWifiStateReceiver() {
        val wifiFilter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    val wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN
                    )
                    when (wifiState) {
                        WifiManager.WIFI_STATE_ENABLED -> wifiViewModel.updateWifiState("ON")
                        WifiManager.WIFI_STATE_DISABLED -> wifiViewModel.updateWifiState("OFF")
                        // 其他状态可以根据需要处理
                    }
                }
            }
        }, wifiFilter)
    }

    private fun setupWifiInfoReceiver() {
        val wifiFilter = IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        }
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

                if (networkCapabilities != null && networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI)) {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo
                    if (wifiInfo != null && wifiInfo.networkId != -1) {
                        // 从wifiInfo获取SSID和BSSID并更新ViewModel
                        val ssid = if (wifiInfo.ssid != null) wifiInfo.ssid.trim('"') else "" // 去除SSID周围的引号
                        val bssid = wifiInfo.bssid ?: ""
                        Log.v("wifi",wifiInfo.toString())
                        wifiViewModel.updateWifiInfo(ssid, bssid)
                    }
                }
            }
        }, wifiFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
    }
}

//TODO: 合并这两个ViewModel，使用不同方法来维护两个state
class BluetoothViewModel : ViewModel() {
    val bluetoothState = MutableLiveData<String>()

    fun initBluetoothState(adapter: BluetoothAdapter?) {
        // 这个方法用来在打开应用的时候手动进行第一次蓝牙状态探测
        bluetoothState.value = when {
            adapter == null -> "Not Supported"
            adapter.isEnabled -> "ON"
            else -> "OFF"
        }
    }

    fun updateBluetoothState(state: String) {
        bluetoothState.value = state
    }
}

class WifiViewModel : ViewModel() {
    val wifiState = MutableLiveData<String>()
    val currentSSID = MutableLiveData<String>()
    val currentBSSID = MutableLiveData<String>()

    fun updateWifiState(state: String) {
        wifiState.value = state
    }

    fun updateWifiInfo(ssid: String, bssid: String) {
        currentSSID.value = ssid
        currentBSSID.value = bssid
    }
}


