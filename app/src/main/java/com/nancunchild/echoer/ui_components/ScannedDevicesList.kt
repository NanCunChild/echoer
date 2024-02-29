package com.nancunchild.echoer.ui_components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Wifi
import com.nancunchild.echoer.R
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nancunchild.echoer.activities.ChatActivity
import com.nancunchild.echoer.adapters.DeviceAdapter

/**
 * 这个类为UI类，是直接显示在HomeScreen上的设备列表
 * 负责了部分列表数据的样式
 */
class ScannedDevicesList {
    @Composable
    fun DevicesList(
        devices: List<DeviceAdapter>
    ) {
        val context = LocalContext.current // 获取当前 Composable 的 Context
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(
                items = devices,
                itemContent = { device ->
                    Device(
                        headlineContent = {
                            when (device.deviceClass) {
                                "bluetooth" -> Text(device.bluetoothName ?: "Unknown BCName")
                                "wifi" -> Text(device.wifiSSID ?: "Unknown SSID")
                                "dual" -> Text(device.wifiSSID ?: "Unknown SSID")
                            }
                        },
                        supportingContent = {
                            when (device.deviceClass) {
                                "bluetooth" -> Text(device.bluetoothAddress ?: "Unknown")
                                "wifi" -> Text(device.wifiBSSID ?: "Unknown")
                                "dual" -> Text(device.wifiSSID ?: "Unknown")
                            }
                        },
                        trailingContent = {
                            when (device.deviceClass) {
                                "bluetooth" -> Text("BC")
                                "wifi" -> Text("WiFi")
                                "dual" -> Text("DUAL")
                            }
                        },
                        leadingContent = {
                            when (device.deviceClass) {
                                "bluetooth" -> Icon(
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    painter = painterResource(id = R.drawable.baseline_bluetooth_24),
                                    contentDescription = "Bluetooth Device"
                                )

                                "wifi" -> when (device.wifiLevel) {
                                    in -50..0 ->
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_wifi_3_24),
                                            contentDescription = "WiFi Device"
                                        )

                                    in -70..-50 ->
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_wifi_2_24),
                                            contentDescription = "WiFi Device"
                                        )

                                    in -100..-70 ->
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_wifi_1_24),
                                            contentDescription = "WiFi Device"
                                        )

                                    else ->
                                        Text("Error")
                                }

                                "dual" -> Icon(
                                    painter = painterResource(id = R.drawable.baseline_bluetooth_wifi_24),
                                    contentDescription = "Unknown Device"
                                )
                            }
                        },
                        onItemClick = {
                            val intent = Intent(context, ChatActivity::class.java).apply {
                                putExtra("deviceName", device.bluetoothName ?: "Unknown")
                                putExtra("deviceAddress", device.bluetoothAddress ?: "Unknown")

                            }
                            context.startActivity(intent)
                        }
                    )
                })
        }
    }

    @Composable
    fun Device(
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
}