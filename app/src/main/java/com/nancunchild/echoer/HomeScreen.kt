package com.nancunchild.echoer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nancunchild.echoer.BluetoothViewModel

@Composable
fun ScreenLayout() {
    // 获取 ViewModel 实例
    val bluetoothViewModel: BluetoothViewModel = viewModel()
    // 观察 ViewModel 中的状态
    val bluetoothState = bluetoothViewModel.bluetoothState.observeAsState("Unknown")

    val wifiViewModel: WifiViewModel = viewModel()
    val wifiState = wifiViewModel.wifiState.observeAsState("Unknown")

    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

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
                    modifier = Modifier
                        .padding(12.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                        Text("WiFi")
                        Text(wifiState.value)
                    }
                },
                onClick = { /* 处理点击事件 */ },
                containerColor = wifiBtnColor,
                contentColor = Color(0xCCFFFFFF),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .height(81.dp)
                    .width(169.dp)
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
                        Text("Bluetooth")
                        Text(bluetoothState.value)
                    }
                },
                onClick = {
                    bluetoothAdapter?.let { adapter ->
                        if (!adapter.isEnabled) {
                            // 启动意图提示用户开启蓝牙
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            context.startActivity(enableBtIntent)
                        } else {
                            Log.d("Bluetooth", "请手动关闭蓝牙或导航到设置页面")
                            // 真的无语了，怎么会有打得开关不掉的神奇设定啊卧槽。BYD谷歌不想让开发者开发就别出标准了
                            // 可以使用下面的代码片段导航到蓝牙设置页面
                            // val intentOpenBluetoothSettings = Intent()
                            // intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
                            // context.startActivity(intentOpenBluetoothSettings)
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
            )
        }
        WireItems(headline = "1")
    }
}

@Composable
fun WireItems(
    icon: @Composable (() -> Unit)? = null,
    headline: String,
    supportingText: String? = null,
    trailingSupportingText: String? = null
) {
    LazyColumn() {
        items(5) { index ->
            WireItem(
                headlineContent = { Text("Item: $index") },
                supportingContent = { Text("Secondary text") },
                trailingContent = { Text("meta") },
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description"
                    )
                },
                onItemClick = {}
            )
        }
    }
}

@Composable
fun WireItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit,
    leadingContent: @Composable () -> Unit,
    onItemClick: () -> Unit // 点击事件的处理函数
) {
    val interactionSource = remember { MutableInteractionSource() }
    ListItem(
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent,
        leadingContent = leadingContent,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = Color.LightGray),
                onClick = onItemClick
            )
            .padding(8.dp)
    )
}