package com.nancunchild.echoer

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var bluetoothStateReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBluetoothStateReceiver()
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
                            }

                            BluetoothAdapter.STATE_TURNING_OFF -> {
                                Log.v("Bluetooth", "Bluetooth Turning OFF")
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
                    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                    when (wifiState) {
                        WifiManager.WIFI_STATE_ENABLED -> wifiViewModel.updateWifiState("ON")
                        WifiManager.WIFI_STATE_DISABLED -> wifiViewModel.updateWifiState("OFF")
                        // 其他状态可以根据需要处理
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

    fun updateBluetoothState(state: String) {
        bluetoothState.value = state
    }
}

class WifiViewModel : ViewModel(){
    val wifiState = MutableLiveData<String>()

    fun updateWifiState(state: String){
        wifiState.value = state
    }
}

