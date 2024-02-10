package com.nancunchild.echoer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nancunchild.echoer.adapters.DeviceAdapter

class ScannerViewModel: ViewModel() {
    private val _bcScanDevices = MutableLiveData<List<DeviceAdapter>>()
    private val _wifiScanDevices = MutableLiveData<List<DeviceAdapter>>()
    val bcScanDevices: LiveData<List<DeviceAdapter>> = _bcScanDevices
    val wifiScanDevices: LiveData<List<DeviceAdapter>> = _wifiScanDevices

    fun updateBCScannedDevices(device: DeviceAdapter) {
        val currentList = _bcScanDevices.value ?: emptyList()
        _bcScanDevices.value = currentList + device
    }

    // 在每一轮扫描开始时调用，删除上一轮的扫描结果
    fun clearBCScannedDevices() {
        _bcScanDevices.value = emptyList()
    }

    fun updateWiFiScannedDevices(device: DeviceAdapter){
        val currentList = _wifiScanDevices.value ?: emptyList()
        _wifiScanDevices.value = currentList + device
    }

    fun clearWiFiScannedDevices(){
        _wifiScanDevices.value = emptyList()
    }
}