package com.nancunchild.echoer.ui_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nancunchild.echoer.adapters.DeviceAdapter

class ScannedDevicesList {
    @Composable
    fun DevicesList(
        devices: List<DeviceAdapter>
    ) {
        LazyColumn {
            items(
                items = devices,
                itemContent = { device ->
                Device(
                    headlineContent = { Text(device.bluetoothName ?: "Unknown Device" )},
                    supportingContent = { Text(device.bluetoothAddress ?: "No Data") },
                    trailingContent = { Text("BC") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Devices"
                        )
                    },
                    onItemClick = {}
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