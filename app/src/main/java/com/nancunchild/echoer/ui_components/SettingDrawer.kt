package com.nancunchild.echoer.ui_components

/**
 * TODO:准备在这个类里面放置设置弹出的抽屉
 */
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.ModalDrawer
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//
//class SettingDrawer {
//    @Composable
//    fun DrawerFilm() {
//        val drawerState = DrawerViewModel().drawerState.value
//        ModalDrawer(
//            drawerState = drawerState,
//            drawerContent = {
//                DrawerContent(onItemClicked = { /* 处理抽屉项点击事件 */ })
//            }
//        ) {
//            Scaffold(
//                topBar = {
//                    TopAppBar(
//                        title = { Text("Main Screen") },
//                        actions = {
//                            IconButton(onClick = { }) {
//                                Icon(Icons.Filled.Settings, contentDescription = "Settings")
//                            }
//                        }
//                    )
//                },
//                content = {
//                    InnerScreenContent()
//                }
//            )
//        }
//    }
//
//    @Composable
//    fun DrawerContent(onItemClicked: () -> Unit) {
//        Column {
//            // 在这里添加抽屉的内容
//            Text("Item 1", modifier = Modifier.clickable { onItemClicked() })
//            Text("Item 2", modifier = Modifier.clickable { onItemClicked() })
//            // 添加更多项...
//        }
//    }
//
//    @Composable
//    fun InnerScreenContent() {
//        // 这里是主屏幕的内容
//        Column(modifier = Modifier.fillMaxSize()) {
//            Text("This is the main content area.")
//            // 添加更多内容...
//        }
//    }
//
//}
//
//class DrawerViewModel : ViewModel() {
//    // 抽屉状态，true 表示打开，false 表示关闭
//    private val _drawerState = mutableStateOf(false)
//    val drawerState: State<Boolean> = _drawerState
//
//    // 打开抽屉
//    fun openDrawer() {
//        _drawerState.value = true
//    }
//
//    // 关闭抽屉
//    fun closeDrawer() {
//        _drawerState.value = false
//    }
//}
//
