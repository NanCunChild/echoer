package com.nancunchild.echoer.viewmodels

import android.util.Log
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
    private val _allScanDevices = MutableLiveData<List<DeviceAdapter>>()
//    val bcScanDevices: LiveData<List<DeviceAdapter>> = _bcScanDevices
//    val wifiScanDevices: LiveData<List<DeviceAdapter>> = _wifiScanDevices
    val allScanDevices: LiveData<List<DeviceAdapter>> = _allScanDevices

    init {
        // 保证两个列表在变更时能实时监测到
        _bcScanDevices.observeForever { mergeDevices() }
        _wifiScanDevices.observeForever { mergeDevices() }
    }

    private fun mergeDevices() {
        val currentBCDevices = _bcScanDevices.value ?: emptyList()
        val currentWiFiDevices = _wifiScanDevices.value ?: emptyList()
        val mergedList = mutableListOf<DeviceAdapter>()

        // 先添加所有 BC 设备
        mergedList.addAll(currentBCDevices)

        // 遍历 WiFi 设备，查找并合并或添加
        currentWiFiDevices.forEach { wifiDevice ->
            val matchingBCDeviceIndex = currentBCDevices.indexOfFirst { bcDevice ->
                bcDevice.bluetoothAddress == wifiDevice.wifiBSSID
            }

            if (matchingBCDeviceIndex != -1) {
                // 找到匹配项，进行合并
                val matchingBCDevice = currentBCDevices[matchingBCDeviceIndex]
                val mergedDevice = mergeDeviceData(matchingBCDevice, wifiDevice)
                // 先从列表中移除匹配的蓝牙设备
                mergedList.removeAt(matchingBCDeviceIndex)
                // 然后将合并后的设备插入到列表的开头
                mergedList.add(0, mergedDevice)
                Log.v("ScannerViewModel", "Merged and moved to top: $mergedDevice")
            } else {
                // 没有找到匹配项，直接添加 WiFi 设备
                if (mergedList.none { it.wifiBSSID == wifiDevice.wifiBSSID }) {
                    mergedList.add(wifiDevice)
                }
            }
        }

        _allScanDevices.value = mergedList
    }

    private fun mergeDeviceData(bcDevice: DeviceAdapter, wifiDevice: DeviceAdapter): DeviceAdapter {
        // 示例合并逻辑，根据实际情况调整
        return DeviceAdapter(
            bluetoothName = bcDevice.bluetoothName ?: wifiDevice.wifiSSID,
            bluetoothAddress = bcDevice.bluetoothAddress,
            bluetoothClass = bcDevice.bluetoothClass,
            bluetoothBondState = bcDevice.bluetoothBondState,
            wifiSSID = wifiDevice.wifiSSID,
            wifiBSSID = wifiDevice.wifiBSSID,
            wifiFrequency = wifiDevice.wifiFrequency,
            wifiLevel = wifiDevice.wifiLevel,
            deviceClass = "dual" // 表示该设备同时被识别为 Wi-Fi 和蓝牙设备
        )
    }

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