package com.nancunchild.echoer.activities

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import com.nancunchild.echoer.fragments.HomeScreen
import com.nancunchild.echoer.viewmodels.BluetoothStatusViewModel
import com.nancunchild.echoer.viewmodels.WiFiStatusViewModel
import com.nancunchild.echoer.services.BluetoothStatusMonitor
import com.nancunchild.echoer.services.WiFiStatusMonitor
import com.nancunchild.echoer.services.BluetoothScanner
import com.nancunchild.echoer.utils.PermissionManager

class MainActivity : ComponentActivity() {
    private val bluetoothStatusViewModel: BluetoothStatusViewModel by viewModels()
    private val wifiStatusViewModel: WiFiStatusViewModel by viewModels()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothStatusMonitor: BluetoothStatusMonitor
    private lateinit var wifiStatusMonitor:WiFiStatusMonitor

    private lateinit var bluetoothScanner: BluetoothScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 此处是为了手动执行一次蓝牙检测
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothStatusViewModel.initBluetoothState(bluetoothAdapter)

        // 初始化蓝牙状态监视器
        bluetoothStatusMonitor = BluetoothStatusMonitor(this, bluetoothStatusViewModel)
        bluetoothStatusMonitor.startMonitoring()
        // 初始化wifi状态监视器
        wifiStatusMonitor = WiFiStatusMonitor(this,wifiStatusViewModel)
        wifiStatusMonitor.startMonitoring()



        setContent {
            val context = LocalContext.current
            HomeScreen().ScreenLayout()
        }
    }

    override fun onResume() {
        super.onResume()
        bluetoothStatusMonitor.startMonitoring()
        wifiStatusMonitor.startMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothStatusMonitor.stopMonitoring()
        wifiStatusMonitor.stopMonitoring()
    }
}



