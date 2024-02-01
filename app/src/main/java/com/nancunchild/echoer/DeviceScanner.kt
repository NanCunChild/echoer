import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult as BluetoothScanResult
import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Scanner
import android.net.wifi.ScanResult as WifiScanResult

class DeviceScanner(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
//    private val wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @SuppressLint("MissingPermission")
    fun startBluetoothScan(callback: (List<BluetoothScanResult>) -> Unit, errorCallback: (Int) -> Unit) {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: BluetoothScanResult?) {
                result?.let { callback(listOf(it)) }
            }

            override fun onBatchScanResults(results: List<BluetoothScanResult>) {
                callback(results)
            }

            override fun onScanFailed(errorCode: Int) {
                errorCallback(errorCode)
            }
        }

        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
    }

//    @SuppressLint("MissingPermission")
//    fun startWifiScan(callback: (List<WifiScanResult>) -> Unit, errorCallback: () -> Unit) {
//        if (wifiManager.startScan()) {
//            val results = wifiManager.scanResults
//            callback(results)
//        } else {
//            errorCallback()
//        }
//    }
}

class ScannerViewModel(private val scanner: DeviceScanner) : ViewModel() {

//    private val _wifiState = MutableStateFlow("未扫描")
//    val wifiState = _wifiState.asStateFlow()

    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val bluetoothDevices = _bluetoothDevices.asStateFlow()


//    fun startWifiScan() {
//        viewModelScope.launch {
//            scanner.startWifiScan(
//                callback = { wifiResults ->
//                    // 更新WiFi扫描状态
//                    _wifiState.value = "找到 ${wifiResults.size} 个网络"
//                },
//                errorCallback = {
//                    // 处理扫描错误
//                    _wifiState.value = "扫描失败"
//                }
//            )
//        }
//    }

    fun startBluetoothScan() {
        viewModelScope.launch {
            scanner.startBluetoothScan(
                callback = { results ->
                    // 更新蓝牙扫描结果
                    _bluetoothDevices.value = results.map { it.device }
                },
                errorCallback = { errorCode ->
                    // 这里可以处理扫描错误，例如更新状态或记录日志
                }
            )
        }
    }
}
