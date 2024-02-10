package com.nancunchild.echoer.adapters

import android.bluetooth.BluetoothDevice

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
        fun fromBCScanResult(device: BluetoothDevice): DeviceAdapter {
            return DeviceAdapter(
                bluetoothName = device.name,
                bluetoothAddress = device.address,
                bluetoothClass = device.bluetoothClass.toString(),
                deviceClass = "bluetooth"
            )
        }

        fun fromWiFiScanResult(scanResult: android.net.wifi.ScanResult): DeviceAdapter {
            return DeviceAdapter(
                wifiSSID = scanResult.SSID,
                wifiBSSID = scanResult.BSSID,
                wifiFrequency = scanResult.frequency,
                wifiLevel = scanResult.level,
                deviceClass = "wifi"
            )
        }

        fun mergeDual(deviceBCAdapter: DeviceAdapter, deviceWiFiAdapter: DeviceAdapter) {

        }
    }
}
