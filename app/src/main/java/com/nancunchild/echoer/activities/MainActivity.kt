package com.nancunchild.echoer.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.bluetooth.BluetoothManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nancunchild.echoer.fragments.HomeFragment
import com.nancunchild.echoer.viewmodels.BluetoothStatusViewModel
import com.nancunchild.echoer.viewmodels.WiFiStatusViewModel
import com.nancunchild.echoer.services.BluetoothStatusMonitor
import com.nancunchild.echoer.services.WiFiStatusMonitor
import com.nancunchild.echoer.services.BCScanner
import com.nancunchild.echoer.ui.theme.EchoerTheme
import com.nancunchild.echoer.utils.PermissionManager
import com.nancunchild.echoer.viewmodels.ScannerViewModel

/**
 * 主活动类，是应用的入口点。负责初始化和管理应用的核心组件，包括权限请求、蓝牙和 Wi-Fi 状态监视器、以及 UI 设置。
 *
 * 在 onCreate 方法中，应用会请求一系列运行时权限，这些权限对于应用的正常运行是必需的。权限请求的结果会决定是否初始化硬件适配器和状态监视器。
 * 如果所有需要的权限都已授予，应用将继续初始化蓝牙和 Wi-Fi 适配器，并开始监视它们的状态。否则，应用将等待用户授予权限。
 *
 * 此外，MainActivity 还处理了应用的生命周期事件，如 onResume 和 onDestroy，以确保在应用运行期间正确地开始和停止硬件设备的监视。
 */
class MainActivity : ComponentActivity() {
    private val bluetoothStatusViewModel: BluetoothStatusViewModel by viewModels()
    private val wifiStatusViewModel: WiFiStatusViewModel by viewModels()
    private val scannerViewModel: ScannerViewModel by viewModels()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothStatusMonitor: BluetoothStatusMonitor
    private lateinit var wifiStatusMonitor: WiFiStatusMonitor

    private lateinit var mBCScanner: BCScanner

    private lateinit var permissionManager: PermissionManager

    /**
     * 在活动创建时调用。初始化权限管理器，请求必要的运行时权限，并设置 UI 布局。
     *
     * @param savedInstanceState 如果活动之前被终止，则包含活动之前的状态信息的 Bundle；否则为 null。
     */
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
            Log.v("PermissionManager", "All Permissions granted.")
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
            var isDarkMode by remember { mutableStateOf(false) }
            EchoerTheme(darkTheme = isDarkMode) {
                HomeFragment().ScreenLayout(
                    isDarkMode = isDarkMode,
                    onThemeUpdated = { isDarkMode = !isDarkMode }
                )
                HomeFragment().DoubleBackToExit(this)
                HomeFragment().InitialScanning()
            }

//            Button(onClick = {
//                val intent = Intent(this@MainActivity, ChatActivity::class.java)
//                startActivity(intent)
//            }) {
//                Text(text = "GoToChat(debug)")
//            }
        }
    }

    /**
     * 初始化硬件适配器和状态监视器。此方法在所有必要的权限被授予后调用。
     * 它负责设置蓝牙管理器、蓝牙和 Wi-Fi 状态监视器，以及蓝牙扫描器。
     */
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
        mBCScanner = BCScanner(this, scannerViewModel)
        if (bluetoothAdapter.isEnabled) {
            Log.v("BluetoothScan", "Bluetooth Is Enabled. Start Scanning...")
            mBCScanner.startScanning()
        } else {
            Log.v("BluetoothScan", "Bluetooth Is Not Enabled.")
        }
    }

    /**
     * 在活动恢复时调用。确保在活动重新成为用户可见时，重新开始监视蓝牙和 Wi-Fi 状态，以及停止蓝牙扫描。
     */
    override fun onResume() {
        super.onResume()
        bluetoothStatusMonitor.startMonitoring()
        wifiStatusMonitor.startMonitoring()
        mBCScanner.stopScanning()
    }

    /**
     * 在活动销毁时调用。确保在活动销毁前停止所有的硬件状态监视和蓝牙扫描，以释放资源。
     */
    override fun onDestroy() {
        super.onDestroy()
        bluetoothStatusMonitor.stopMonitoring()
        wifiStatusMonitor.stopMonitoring()
        mBCScanner.stopScanning()
    }
}



