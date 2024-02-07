package com.nancunchild.echoer.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nancunchild.echoer.adapters.DeviceAdapter

class BluetoothScannerViewModel : ViewModel() {
    private val _scannedDevices = MutableLiveData<List<DeviceAdapter>>()
    val scannedDevices: LiveData<List<DeviceAdapter>> = _scannedDevices

    fun updateScannedDevices(device: DeviceAdapter) {
        val currentList = _scannedDevices.value ?: emptyList()
        _scannedDevices.value = currentList + device
    }

    // 在每一轮扫描开始时调用，删除上一轮的扫描结果
    fun clearScannedDevices() {
        _scannedDevices.value = emptyList()
    }
}
