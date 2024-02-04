package com.nancunchild.echoer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import androidx.lifecycle.asLiveData
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult as BluetoothScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceScanner(context: Context) {
    private val appContext = context.applicationContext
    private var receiver: BroadcastReceiver? = null
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    // LiveData to expose discovered devices
    private val _discoveredDevices = MutableLiveData<List<BluetoothDevice>>()
    val discoveredDevices: LiveData<List<BluetoothDevice>> = _discoveredDevices

    fun startBluetoothClassicScan(errorCallback: () -> Unit) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            _discoveredDevices.value = _discoveredDevices.value.orEmpty() + it
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        unregisterReceiver()
                    }
                }
            }
        }

        IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }.also { filter ->
            appContext.registerReceiver(receiver, filter)
        }

        bluetoothAdapter?.cancelDiscovery()
        if (bluetoothAdapter?.startDiscovery() != true) {
            errorCallback()
        }
    }

    fun stopBluetoothClassicScan() {
        bluetoothAdapter?.cancelDiscovery()
        unregisterReceiver()
    }

    private fun unregisterReceiver() {
        receiver?.let {
            appContext.unregisterReceiver(it)
            receiver = null
        }
    }
}

class ScannerViewModel(private val scanner: DeviceScanner) : ViewModel() {

    // 直接使用 scanner 中的 LiveData
    val bluetoothClassicDevices = scanner.discoveredDevices

    fun startScanning() {
        scanner.startBluetoothClassicScan {
            // Handle error case here
        }
    }

    fun stopScanning() {
        scanner.stopBluetoothClassicScan()
    }

    override fun onCleared() {
        super.onCleared()
        scanner.stopBluetoothClassicScan()
    }
}
