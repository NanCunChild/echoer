package com.nancunchild.echoer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WiFiStatusViewModel : ViewModel() {
    val _wifiState = MutableLiveData<String>()
    val _currentSSID = MutableLiveData<String>()
    val _currentBSSID = MutableLiveData<String>()
    val wifiState: LiveData<String> = _wifiState
    val currentSSID: LiveData<String> = _currentSSID
    val currentBSSID: LiveData<String> = _currentBSSID

    fun updateWifiState(state: String) {
        _wifiState.value = state
    }

    fun updateWifiInfo(ssid: String, bssid: String) {
        _currentSSID.value = ssid
        _currentBSSID.value = bssid
    }
}