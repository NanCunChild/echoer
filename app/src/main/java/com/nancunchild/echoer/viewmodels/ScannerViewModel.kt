package com.nancunchild.echoer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nancunchild.echoer.adapters.DeviceAdapter

/**
 * 这个类用于使用ViewModel存储扫描结果，也是负责从逻辑到UI的传递
 * 注意：
 * 蓝牙返回结果为单个出现，每次发现一个
 * WiFi扫描结果是成组出现，每次发现一组
 * 因此两个的存储逻辑稍有区别
 */
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

    fun updateWiFiScannedDevices(devices: List<DeviceAdapter>){
        _wifiScanDevices.value = devices
    }

    fun clearWiFiScannedDevices(){
        _wifiScanDevices.value = emptyList()
    }
}