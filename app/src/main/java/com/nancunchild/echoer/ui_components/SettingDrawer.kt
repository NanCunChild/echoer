package com.nancunchild.echoer.ui_components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nancunchild.echoer.R
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

/**
 * TODO:准备在这个类里面放置设置弹出的抽屉
 */

class SettingDrawer {
    @Composable
    fun Drawer() {
        DrawerHeader()
        DrawerBody(
            items = listOf(
                DrawerBodyItem(
                    id = "home",
                    title = "Home",
                    contentDescription = "Go to home screen",
                    icon = Icons.Default.Home
                ),
                DrawerBodyItem(
                    id = "settings",
                    title = "Settings",
                    contentDescription = "Go to settings screen",
                    icon = Icons.Default.Settings
                ),
                DrawerBodyItem(
                    id = "help",
                    title = "Help",
                    contentDescription = "Get help",
                    icon = Icons.Default.Info
                ),
            ),
            onItemClick = {
                println("Clicked on ${it.title}")
            }
        )
    }

    @Composable
    fun DrawerHeader() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 64.dp),
            contentAlignment = Alignment.Center
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .border(
                            shape = CircleShape,
                            border = BorderStroke(4.dp, Color.Black),
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // val image: Painter = painterResource(id = R.drawable.echoer_main_screen_logo)
                    Image(
                        // painter = image,
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Echoer",
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Username",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800,
                        modifier = Modifier.padding(6.dp),
                    )
                    // 添加个人信息的文本组件
                }
            }
        }
    }

    @Composable
    fun DrawerBody(
        items: List<DrawerBodyItem>,
        modifier: Modifier = Modifier,
        itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
        onItemClick: (DrawerBodyItem) -> Unit
    ) {
        val selectedItem = remember { mutableStateOf<DrawerBodyItem?>(null) }

//        // using Row
//        LazyColumn(modifier) {
//            items(items) { item ->
//                val isSelected = selectedItem.value == item
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            selectedItem.value = item
//                            onItemClick(item)
//                        }
//                        .padding(16.dp)
//                        .background(
//                            if (isSelected) Color.LightGray else Color.Transparent,
//                            RoundedCornerShape(4.dp)
//                        ),
//                ) {
//                    Icon(
//                        imageVector = item.icon,
//                        contentDescription = item.contentDescription
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Text(
//                        text = item.title,
//                        style = itemTextStyle,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }
//        }

        // using ListItem

        LazyColumn {
            items(
                items = items,
                itemContent = { item ->
                    val isSelected = selectedItem.value == item
                    DrawerBodyItem(item, isSelected, onItemClick = {})
                })
        }
    }

    data class DrawerBodyItem(
        val id: String,
        val title: String,
        val contentDescription: String,
        val icon: ImageVector
    )

        @Composable
        fun DrawerBodyItem(
            item: DrawerBodyItem,
            isSelected: Boolean,
            onItemClick: (DrawerBodyItem) -> Unit
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            ListItem(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true, color = Color.LightGray),
                        onClick = { onItemClick(item) }
                    )
                    .padding(3.dp)
                    .background(Color.White),
                leadingContent = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription
                    )
                },
                headlineContent = {
                    Text(
                        text = item.title,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            )
        }
}