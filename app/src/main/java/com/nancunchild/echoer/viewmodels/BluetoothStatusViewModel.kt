package com.nancunchild.echoer.viewmodels

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothStatusViewModel : ViewModel() {
    // 此处大费周章地定义一个_bluetoothState，是为了对外暴露的变量是LiveData，无法被外界直接篡改
    private val _bluetoothState = MutableLiveData<String>()
    val bluetoothState: LiveData<String> = _bluetoothState

    fun initBluetoothState(adapter: BluetoothAdapter?) {
        // 这个方法用来在打开应用的时候手动进行第一次蓝牙状态探测
        _bluetoothState.value = when {
            adapter == null -> "Not Supported"
            adapter.isEnabled -> "ON"
            else -> "OFF"
        }
    }

    fun updateBluetoothState(state: String) {
        _bluetoothState.value = state
    }
}