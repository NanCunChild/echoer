package com.nancunchild.echoer.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothScannerViewModel : ViewModel() {
    private val _scannedDevices = MutableLiveData<List<BluetoothDevice>>()
    val scannedDevices: LiveData<List<BluetoothDevice>> = _scannedDevices

    fun updateScannedDevices(device: BluetoothDevice) {
        val currentList = _scannedDevices.value ?: emptyList()
        _scannedDevices.value = currentList + device
    }
}
