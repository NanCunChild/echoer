package com.nancunchild.echoer.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.nancunchild.echoer.viewmodels.BluetoothScannerViewModel

class BluetoothScanner(
    private val context: Context,
    private val viewModel: BluetoothScannerViewModel
) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action ?: return
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        viewModel.updateScannedDevices(it)
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    stopScanning() // Optional: Stop scanning once discovery is finished
                }
            }
        }
    }

    fun startScanning() {
        if (bluetoothAdapter?.isEnabled == true) {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            context.registerReceiver(receiver, filter)
            bluetoothAdapter.startDiscovery()
        } else {
            Log.e("BluetoothScanner", "Bluetooth is not enabled")
        }
    }

    fun stopScanning() {
        bluetoothAdapter?.cancelDiscovery()
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            Log.e("BluetoothScanner", "Receiver not registered")
        }
    }
}
