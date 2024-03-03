package com.nancunchild.echoer.fragments

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nancunchild.echoer.R
import com.nancunchild.echoer.services.BCScanner
import com.nancunchild.echoer.services.WiFiScanner
import com.nancunchild.echoer.ui_components.ScannedDevicesList
import com.nancunchild.echoer.viewmodels.BluetoothStatusViewModel
import com.nancunchild.echoer.viewmodels.WiFiStatusViewModel
import com.nancunchild.echoer.viewmodels.ScannerViewModel

import androidx.compose.runtime.*
import com.nancunchild.echoer.ui_components.SettingDrawer
import kotlinx.coroutines.launch


class HomeFragment : ComponentActivity() {
    private lateinit var mBCScanner: BCScanner
    private lateinit var wifiScanner: WiFiScanner

    @SuppressLint("MissingPermission")
    @Composable
    fun ScreenLayout(
        isDarkMode: Boolean = false,
        onThemeUpdated: () -> Unit,
    ) {
//        var isDarkMode by remember { mutableStateOf(false) }
//        EchoerTheme(darkTheme = isDarkMode) {
            val context = LocalContext.current
            // 获取 ViewModel 实例
            val bluetoothViewModel: BluetoothStatusViewModel = viewModel()
            // 观察 ViewModel 中的状态
            val bluetoothState = bluetoothViewModel.bluetoothState.observeAsState("Unknown")

            // 同理，注册wifi的ViewModel，然后观察是否连接以及连接的wifi信息
            val currentWiFiStateViewModel: WiFiStatusViewModel = viewModel()
            val currentWiFiState = currentWiFiStateViewModel.wifiState.observeAsState("Unknown")
            val currentWiFiSSID = currentWiFiStateViewModel.currentSSID.observeAsState("Unknown")

            // 存储蓝牙调用上下文
            val bluetoothManager =
                context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

            val scannerViewModel: ScannerViewModel = viewModel()
            val allDevices = scannerViewModel.allScanDevices.observeAsState(emptyList())

            // 创建抽屉的状态对象
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()


            // 使用ModalDrawer创建抽屉
            ModalDrawer(
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerState = scaffoldState.drawerState,
                drawerContentColor = Color.White,
                drawerContent = { SettingDrawer().Drawer() }
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ){
                    Row(    // top bar
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 设置按钮
                        IconButton(
                            onClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Menu, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = "Open Settings")
                        }

                        // 夜间模式切换按钮
                        IconButton(
                            onClick = onThemeUpdated
                        ) {
                            val image = if (isDarkMode) {
                                Icons.Filled.Nightlight
                            } else {
                                Icons.Filled.LightMode
                            }
                            Icon(imageVector = image, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = "Toggle Night Mode")
                        }
                    }

                    Row(    // Header
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val image: Painter =
                                painterResource(id = R.drawable.echoer_main_screen_logo)
                            Image(
                                painter = image,
                                contentDescription = "Echoer",
                                modifier = Modifier
                                    .height(64.dp)
                                    .width(64.dp)
                            )
                            Text(
                                text = "ECHOER",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W800,
                                modifier = Modifier
                                    .padding(6.dp),
                            )
                        }
                    }

                    Row(    // Wifi and bluetooth button
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExtendedFloatingActionButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Wifi,
                                    contentDescription = "WiFi",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        text = "WiFi " + currentWiFiState.value,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = currentWiFiSSID.value,
                                        fontWeight = FontWeight.W200,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            },
                            onClick = {
                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                context.startActivity(intent)
                            },
                            containerColor = when (currentWiFiState.value) {
                                "ON" -> Color(0xFF1E90FF)
                                "OFF" -> Color(0xFFD0D0D0)
                                else -> Color(0xFFD0D0D0)
                            },
                            contentColor = Color(0xCCFFFFFF),
                            modifier = Modifier
                                .height(88.dp)
                                .width(178.dp)
                                .padding(6.dp)
                        )
                        ExtendedFloatingActionButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Bluetooth,
                                    contentDescription = "Bluetooth",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        text = "Bluetooth " + bluetoothState.value,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "TEXT",
                                        fontWeight = FontWeight.W200,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            },
                            onClick = {
                                bluetoothAdapter?.let { adapter ->
                                    if (!adapter.isEnabled) {
                                        // 启动意图提示用户开启蓝牙
                                        try {
                                            val enableBtIntent =
                                                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                            context.startActivity(enableBtIntent)
                                        } catch (e: Exception) {
                                            Log.v("OpenSetting", "Error in Intent$e")
                                        }

                                    } else {
                                        Log.d("Bluetooth", "请手动关闭蓝牙或导航到设置页面")
                                        // 真的无语了，怎么会有打得开关不掉的神奇设定啊卧槽。BYD谷歌不想让开发者开发就别出标准了
                                        // 可以使用下面的代码片段导航到蓝牙设置页面
                                        val disableBtIntent =
                                            Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                        context.startActivity(disableBtIntent)
                                    }
                                } ?: run {
                                    // adapter 为 null，处理没有蓝牙适配器的情况
                                    // 不会现在还有安卓设备没有蓝牙吧？没有蓝牙你用个锤子echoer
                                    Log.d("Bluetooth", "No Bluetooth adapter found.")
                                }
                            },

                            containerColor = when (bluetoothState.value) {
                                "ON" -> Color(0xFF1E90FF)
                                "OFF" -> Color(0xFFD0D0D0)
                                else -> Color(0xFFD0D0D0)
                            },
                            contentColor = Color(0xCCFFFFFF),
                            modifier = Modifier
                                .height(88.dp)
                                .width(178.dp)
                                .padding(6.dp)
                        )
                    }

                    Row(// Debug Functions
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                Button(onClick = {
//                    wifiScanner = WiFiScanner(context, scannerViewModel)
//                    wifiScanner.startScanning()
//                }) {
//                    Text(text = "WiFi Scan (Test)")
//                }
//                Button(onClick = {
//                    mBCScanner = BCScanner(context, scannerViewModel)
//                    mBCScanner.startScanning()
//                }) {
//                    Text(text = "Bluetooth Scan")
//                }
                        Spacer(modifier = Modifier)

                        ExtendedFloatingActionButton(
                            onClick = {
                                mBCScanner = BCScanner(context, scannerViewModel)
                                wifiScanner = WiFiScanner(context, scannerViewModel)
                                mBCScanner.startScanning()
                                wifiScanner.startScanning()
                            },
                            modifier = Modifier
                                .height(52.dp)
                                .width(144.dp)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Fresh Devices",
                                modifier = Modifier
                                    .padding(2.dp)
                            )
                            Text(
                                text = "Refresh",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(2.dp)
                            )
                        }
                    }
                    ScannedDevicesList().DevicesList(allDevices.value, isDarkMode = isDarkMode)
                }
            }
//        }
    }


    @Composable
    fun DoubleBackToExit(currentActivity: ComponentActivity) {
        // 获取当前的 Context
        val context = LocalContext.current
        // 使用 remember 保存上一次点击返回键的时间
        var lastBackPressTime by remember { mutableLongStateOf(0L) }

        // 使用 BackHandler 捕获返回键事件
        BackHandler {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime > 2000) {
                // 如果两次点击间隔超过 2000 毫秒（2 秒），则不退出应用，而是提示用户
                Toast.makeText(context, "Tap Again To Exit.", Toast.LENGTH_SHORT).show()
                lastBackPressTime = currentTime
            } else {
                // 如果两次点击间隔小于 2000 毫秒，则退出应用
                currentActivity.finish()
            }
        }
    }

    @Composable
    fun InitialScanning(){
        val context = LocalContext.current
        val scannerViewModel: ScannerViewModel = viewModel()
        mBCScanner = BCScanner(context, scannerViewModel)
        wifiScanner = WiFiScanner(context, scannerViewModel)
        mBCScanner.startScanning()
        wifiScanner.startScanning()
    }

}

