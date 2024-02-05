package com.nancunchild.echoer.services

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.nancunchild.echoer.viewmodels.BluetoothStatusViewModel

class BluetoothStatusMonitor(private val context: Context, private val viewModel: BluetoothStatusViewModel) {
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { bluetoothIntent ->
                when (bluetoothIntent.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state = bluetoothIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        handleBluetoothStateChange(state)
                    }
                }
            }
        }
    }

    fun startMonitoring() {
        // try，让我保持优雅。让我在 error 面前 乱云飞渡仍从容，调试记得看Logcat！
        try {
            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            context.registerReceiver(bluetoothStateReceiver, filter)
        } catch (e: IllegalArgumentException) {
            Log.e("BluetoothStatusMonitor", "Error starting monitoring: ${e.message}")
        }
    }

    fun stopMonitoring() {
        try {
            context.unregisterReceiver(bluetoothStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.e("BluetoothStatusMonitor", "Error stopping monitoring: ${e.message}")
        }
    }

    private fun handleBluetoothStateChange(state: Int) {
        val bluetoothState = when (state) {
            BluetoothAdapter.STATE_OFF -> "OFF"
            BluetoothAdapter.STATE_ON -> "ON"
            BluetoothAdapter.STATE_TURNING_ON -> "Turning ON"
            BluetoothAdapter.STATE_TURNING_OFF -> "Turning OFF"
            BluetoothAdapter.STATE_CONNECTED -> "Connected"
            BluetoothAdapter.STATE_CONNECTING -> "Connecting"
            BluetoothAdapter.STATE_DISCONNECTED -> "Disconnected"
            BluetoothAdapter.STATE_DISCONNECTING -> "Disconnecting"
            else -> "Unknown"
        }

        Log.v("BluetoothStatusMonitor", "Bluetooth state changed to $bluetoothState")
        viewModel.updateBluetoothState(bluetoothState)
    }
}
