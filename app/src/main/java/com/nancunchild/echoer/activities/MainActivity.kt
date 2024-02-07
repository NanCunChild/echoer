package com.nancunchild.echoer.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.bluetooth.BluetoothManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import com.nancunchild.echoer.viewmodels.BluetoothScannerViewModel

class MainActivity : ComponentActivity() {
    private val bluetoothStatusViewModel: BluetoothStatusViewModel by viewModels()
    private val wifiStatusViewModel: WiFiStatusViewModel by viewModels()
    private val bluetoothScannerViewModel: BluetoothScannerViewModel by viewModels()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothStatusMonitor: BluetoothStatusMonitor
    private lateinit var wifiStatusMonitor: WiFiStatusMonitor

    private lateinit var bluetoothScanner: BluetoothScanner

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val basicPermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
        // 根据不同的API版本添加不同的权限，这个后台获取位置的权限很特殊，只能让用户自己去打开，所以需要在应用完全启动之后再谈这个事情
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            // Android 10 (API 级别 29) 或更高版本的特定权限
//            basicPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 11 (API 级别 31) 以上的权限
            basicPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            basicPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        val completePermissions = basicPermissions.toTypedArray()
        // 初始化PermissionManager
        permissionManager = PermissionManager(
            this,
            completePermissions
        )
        permissionManager.requestPermissions()


        if (permissionManager.hasPermissions().isEmpty()) {
            initializeAdapters()
        } else {
            permissionManager.setPermissionAuthResultActor(object :
                PermissionManager.PermissionAuthResultActor {
                override fun onPermissionsGranted() {
                    // 权限被授予时的操作
                    Toast.makeText(this@MainActivity, "Permissions Granted", Toast.LENGTH_SHORT)
                        .show()
                    initializeAdapters()
                }

                override fun onPermissionsDenied(permissionDenied: Array<String>) {
                    // 权限被拒绝时的操作
                    Toast.makeText(
                        this@MainActivity,
                        "Permissions denied: ${permissionDenied.joinToString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            // 请求权限
            permissionManager.requestPermissions()
        }

        setContent {
            val context = LocalContext.current
            HomeScreen().ScreenLayout()
        }
    }

    fun initializeAdapters() {
        // 此处是为了手动执行一次蓝牙检测
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothStatusViewModel.initBluetoothState(bluetoothAdapter)

        // 初始化蓝牙状态监视器
        bluetoothStatusMonitor = BluetoothStatusMonitor(this, bluetoothStatusViewModel)
        bluetoothStatusMonitor.startMonitoring()

        // 初始化wifi状态监视器
        wifiStatusMonitor = WiFiStatusMonitor(this, wifiStatusViewModel)
        wifiStatusMonitor.startMonitoring()

        // 蓝牙扫描器初始化
        bluetoothScanner = BluetoothScanner(this, bluetoothScannerViewModel)
        bluetoothScanner.startScanning()
    }

    override fun onResume() {
        super.onResume()
        bluetoothStatusMonitor.startMonitoring()
        wifiStatusMonitor.startMonitoring()
        bluetoothScanner.stopScanning()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothStatusMonitor.stopMonitoring()
        wifiStatusMonitor.stopMonitoring()
        bluetoothScanner.stopScanning()
    }
}



