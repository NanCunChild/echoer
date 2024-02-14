package com.nancunchild.echoer.adapters

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

/**
 * 这个类负责对蓝牙和WiFi数据的规范化，将数据转化为方便UI调用的方式，减少UI逻辑的复杂度
 * 大部分参数都有官方手册介绍含义
 * deviceClass有三种情况："bluetooth","wifi","dual"
 */
data class DeviceAdapter(
    val bluetoothName: String? = null,
    val bluetoothAddress: String? = null,
    val bluetoothClass: String? = null,
    val bluetoothBondState: String? = null,
    val wifiSSID: String? = null,
    val wifiBSSID: String? = null,
    val wifiFrequency: Int? = null,
    val wifiLevel: Int? = null,
    val deviceClass: String? = null
) {
    companion object {
        @SuppressLint("MissingPermission")
        fun fromBCScanResult(device: BluetoothDevice): DeviceAdapter {
            return DeviceAdapter(
                bluetoothName = device.name,
                bluetoothAddress = device.address,
                bluetoothClass = device.bluetoothClass.toString(),
                deviceClass = "bluetooth"
            )
        }

        fun fromWiFiScanResult(scanResults: List<android.net.wifi.ScanResult>): List<DeviceAdapter> {
            return scanResults.map { scanResult ->
                DeviceAdapter(
                    wifiSSID = scanResult.SSID,
                    wifiBSSID = scanResult.BSSID,
                    wifiFrequency = scanResult.frequency,
                    wifiLevel = scanResult.level,
                    deviceClass = "wifi"
                )
            }
        }
    }
}
