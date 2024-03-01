package com.nancunchild.echoer.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.nancunchild.echoer.utils.Message
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nancunchild.echoer.activities.MainActivity
import java.io.File
import java.util.UUID
import com.nancunchild.echoer.R
import com.nancunchild.echoer.ui.theme.EchoerTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
//显示消息列表
fun MessageList(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageRow(message = message)
        }
    }
}

@Composable
fun Avatar(@DrawableRes drawableResId: Int) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = "Avatar",
        modifier = Modifier
            .size(40.dp) // 设置头像大小
            .clip(CircleShape) // 将头像裁剪为圆形
    )
}


//渲染单条消息
@Composable
fun MessageRow(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        // 对于非本人发送的消息，先显示头像
        if (!message.isSentByMe) {
            Avatar(drawableResId = message.avatarUrl)
            Spacer(modifier = Modifier.width(8.dp)) // 在头像和消息文本之间添加一些间距
        }

        // 使用Column来组织昵称、时间和消息文本
        Column(
            horizontalAlignment = if (message.isSentByMe) Alignment.End else Alignment.Start,
            modifier = Modifier
                .weight(1f) // 确保Column占据大部分水平空间
        ) {
            if (message.isSentByMe) {
                // 对于本人发送的消息，昵称和时间在消息上方显示
                Text(text = message.author, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = message.timestamp, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp)) // 在头像和消息文本之间添加一些间距

            // 消息气泡
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(
                        color = if (message.isSentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(text = message.content)
            }

            if (!message.isSentByMe) {
                // 对于接收的消息，昵称和时间在消息下方显示
                Text(text = message.author, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = message.timestamp, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }

        // 对于本人发送的消息，头像显示在右侧
        if (message.isSentByMe) {
            Spacer(modifier = Modifier.width(8.dp)) // 在消息文本和头像之间添加一些间距
            Avatar(drawableResId = message.avatarUrl)
        }
    }
}

//包含文本输入框（TextField）和发送按钮（Button）
@Composable
fun UserInput(
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {    // 设置输入框背景色
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
            },
            maxLines = 2,
            cursorBrush = SolidColor(LocalContentColor.current),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            decorationBox = { innerTextField ->
                Column(
                    modifier = Modifier.
                        padding(vertical = 10.dp, horizontal = 10.dp),
                ) {
                    // 输入框
                    Box(
                        modifier = Modifier.
                            padding(horizontal = 10.dp)
                            .height(44.dp),
                    ) {
                        innerTextField()
                    }

                    // 下排的按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 三个站位按钮
                        Row(verticalAlignment = Alignment.CenterVertically)  {
                            IconButton(onClick = {}) { Icon(imageVector = Icons.Filled.Mood, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = null) }
                            IconButton(onClick = {}) { Icon(imageVector = Icons.Filled.InsertPhoto, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = null) }
                            IconButton(onClick = {}) { Icon(imageVector = Icons.Filled.Place, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = null) }
                        }

                        val border = BorderStroke (
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )

                        // Send button
                        Button(
                            onClick = {
                                onSend(text)
                                text = "" // 清空输入框
                            },
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(36.dp),
                            border = border,
                        ) {
                            Text(
                                text = "Send",
                                color = Color.Black,
                            )
                        }
                    }
                }
            }
            )
        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TextField(
//                value = text,
//                onValueChange = { text = it },
//                modifier = Modifier
//                    .padding(start = 32.dp),
//                singleLine = true, // 设置为单行输入模式
//                placeholder = {
//                    Text("请输入消息...") // 显示输入提示文本
//                },
//            )
//            IconButton(
//                onClick = {
//                    if (text.isNotBlank()) { // 检查输入内容非空
//                        onSend(text)
//                        text = "" // 清空输入框
//                    }
//                }
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_send),
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                    contentDescription = "Open Settings"
//                )
//            }
//        }
}


//返回按钮
@Composable
fun BackButton() {
    val context = LocalContext.current

    IconButton(onClick = {
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        context.startActivity(intent)
        (context as? Activity)?.finish()
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back to Main"
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTimeString(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return current.format(formatter)
}



//展示和测试聊天界面
 @Preview(showBackground = true)     // 添加后可以在Studio中预览
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    deviceAddress: String? = "Unknown",
    deviceName: String? = "Unknown",
    isDarkMode: Boolean = false,
) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val context = LocalContext.current // 获取当前Composable的Context
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 使用Scaffold布局来固定输入框和发送按钮在底部
    EchoerTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar (
                    title = { Text(text = deviceName ?: "Unknown", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold) }, // 在Text中直接设置颜色
                    navigationIcon = {
                        // 调用封装好的返回按钮函数
                        BackButton()
                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant, // 设置TopAppBar的背景颜色
//                        titleContentColor = Color.White // 设置标题颜色
//                    )
                    actions = {
                        // 夜间模式切换按钮
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = "Mode info"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                UserInput(onSend = { content ->
                    // 假设使用UUID生成唯一的消息ID，并假设所有消息都是由“我”发送的
                    val newMessage = Message(
                        id = UUID.randomUUID().toString(),
                        author = "Me",
                        content = content,
                        timestamp = getCurrentTimeString(),
                        isSentByMe = true, // 假设消息总是由用户自己发送
                        avatarUrl = R.drawable.ali
                    )
                    // 滚动到新消息
                    coroutineScope.launch {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                    messages = messages + newMessage
                })

            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(color = MaterialTheme.colorScheme.onSurfaceVariant) // 设置背景色
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween // 控制子组件间的垂直布局
            ) {
                MessageList(messages = messages)
            }
        }
    }
}








