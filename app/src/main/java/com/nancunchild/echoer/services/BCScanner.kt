package com.nancunchild.echoer.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nancunchild.echoer.adapters.DeviceAdapter
import com.nancunchild.echoer.viewmodels.ScannerViewModel

@SuppressLint("MissingPermission")
class BluetoothScanner(
    private val context: Context,
    private val viewModel: ScannerViewModel
) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action ?: return
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // 谷歌别这样搞，这样显得我很蠢。旧版本接口废弃，新版本又没有完全取代旧版本。
                    // 请忽略else中的废弃报错，这是我能想到的最好的解决方案
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice
                        }

                    device?.let {
                        val standardizedDeviceData = DeviceAdapter.fromBCScanResult(it)
                        viewModel.updateBCScannedDevices(standardizedDeviceData)
                        Log.v("BCScanner",standardizedDeviceData.toString())
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    stopScanning()
                }
            }
        }
    }

    fun startScanning() {
        if (bluetoothAdapter?.isEnabled == true) {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            context.registerReceiver(receiver, filter)
            viewModel.clearBCScannedDevices()
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
