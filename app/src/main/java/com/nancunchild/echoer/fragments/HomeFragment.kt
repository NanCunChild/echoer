package com.nancunchild.echoer.fragments

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nancunchild.echoer.R
import com.nancunchild.echoer.ui_components.ScannedDevicesList
import com.nancunchild.echoer.viewmodels.BluetoothStatusViewModel
import com.nancunchild.echoer.viewmodels.WiFiStatusViewModel
import com.nancunchild.echoer.viewmodels.BluetoothScannerViewModel

class HomeScreen : ComponentActivity() {
    private val bluetoothScannerViewModel: BluetoothScannerViewModel by viewModels()

    @SuppressLint("MissingPermission")
    @Composable
    fun ScreenLayout() {
        val context = LocalContext.current
        // 获取 ViewModel 实例
        val bluetoothViewModel: BluetoothStatusViewModel = viewModel()
        // 观察 ViewModel 中的状态
        val bluetoothState = bluetoothViewModel.bluetoothState.observeAsState("Unknown")

        // 同理，注册wifi的ViewModel，然后观察是否连接以及连接的wifi信息
        val wifiViewModel: WiFiStatusViewModel = viewModel()
        val wifiState = wifiViewModel.wifiState.observeAsState("Unknown")
        val wifiBSSID = wifiViewModel.currentBSSID.observeAsState("Unknown")
        val wifiSSID = wifiViewModel.currentSSID.observeAsState("Unknown")

        // 存储蓝牙调用上下文
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter


        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val bluetoothBtnColor =
            if (bluetoothState.value == "ON") Color(0xFF1E90FF) else Color(0xFFD0D0D0)
        val wifiBtnColor = if (wifiState.value == "ON") Color(0xFF1D88FF) else Color(0xFFD0D0D0)


        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val image: Painter = painterResource(id = R.drawable.echoer_main_screen_logo)
                    Image(
                        painter = image,
                        contentDescription = "Echoer",
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp)
                    )
                    Text(
                        text = "ECHOER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W800,
                        modifier = Modifier
                            .padding(6.dp),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_wifi_24),
                            contentDescription = "WiFi",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    text = {
                        Column {
                            Text("WiFi " + wifiState.value, fontWeight = FontWeight.W500)
                            Text(wifiSSID.value, fontWeight = FontWeight.W200)
                        }
                    },
                    onClick = {
                        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        context.startActivity(intent)
                    },
                    containerColor = wifiBtnColor,
                    contentColor = Color(0xCCFFFFFF),
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .height(81.dp)
                        .width(169.dp)
                        .padding(6.dp)
                )
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_bluetooth_24),
                            contentDescription = "Bluetooth",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    text = {
                        Column {
                            Text("Bluetooth " + bluetoothState.value, fontWeight = FontWeight.W500)
                            Text("TEXT", fontWeight = FontWeight.W200)
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
                                val disableBtIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                context.startActivity(disableBtIntent)
                            }
                        } ?: run {
                            // adapter 为 null，处理没有蓝牙适配器的情况
                            // 不会现在还有安卓设备没有蓝牙吧？没有蓝牙你用个锤子echoer
                            Log.d("Bluetooth", "No Bluetooth adapter found.")
                        }
                    },

                    containerColor = bluetoothBtnColor,
                    contentColor = Color(0xCCFFFFFF),
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .height(81.dp)
                        .width(169.dp)
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
                Button(onClick = {

                }) {
                    Text(text = "WiFi Scan(System)")
                }
                Button(onClick = {
                }) {
                    Text(text = "Bluetooth Scan (Test)")
                }
            }

            ScannedDevicesList().DevicesList(headline = "1")
        }
    }
}

