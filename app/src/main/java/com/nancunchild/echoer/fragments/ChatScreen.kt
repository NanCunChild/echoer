package com.nancunchild.echoer.theme


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import androidx.compose.material.Icon
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nancunchild.echoer.R
import com.nancunchild.echoer.ui.theme.InputBackground
import com.nancunchild.echoer.ui.theme.Primary
import com.nancunchild.echoer.ui.theme.PrimaryDark
import com.nancunchild.echoer.ui.theme.ReceivedMessageBackground
import com.nancunchild.echoer.ui.theme.SecondaryText

@Composable
fun ChatScreen() {
    // 可以在这里定义整个聊天界面的布局
    ChatInputBackground()
    // 这里可以添加更多与聊天界面相关的 Composable 函数
}

@Composable
fun ChatInputBackground() {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = PrimaryDark, shape = RoundedCornerShape(24.dp)),
        placeholder = { Text("请输入信息", color = Color.White) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = Color.White
        )
    )
}



@Composable
fun TopBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // 设置高度，根据需要调整
            .background(
                color = Color.White, // 使用定义的颜色
                shape = RoundedCornerShape(
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                )
            )
    ) {
        // 在这里添加其他内容，如文本、图标等
    }
}

@Composable
fun CircleBackground() {
    Box(
        modifier = Modifier
            .size(50.dp) // 设置尺寸，确保它是正方形，以形成完美的圆形
            .background(color = InputBackground, shape = CircleShape)
    ) {
        // 这里可以添加其他内容，例如图标或文本
    }
}

@Composable
fun InputBackgroundBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = InputBackground, shape = RoundedCornerShape(8.dp))
    ) {
        content()
    }
}

@Composable
fun ReceivedMessageBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 24.dp))
            .background(color = ReceivedMessageBackground)
    ) {
        content()
    }
}

@Composable
fun SentMessageBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp))
            .background(color = Primary)
    ) {
        content()
    }
}

@Composable
fun MyVectorDrawable() {
    Image(
        painter = painterResource(id = R.drawable.ic_back),
        contentDescription = "Back Button"
    )
}

@Composable
fun BackButtonIconWithTint() {
    Icon(
        painter = painterResource(R.drawable.ic_back),
        contentDescription = "Back",
        tint = Color.Black // 设置图标颜色
    )
}

@Composable
fun InfoIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_info),
        contentDescription = "Info",
        tint = Color.Black // 应用黑色 tint
    )
}

@Composable
fun SendIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_send),
        contentDescription = "Send",
        tint = Color.Black // 应用黑色 tint，与 XML 中的定义一致
    )
}

@Composable
fun ReceivedMessageItem(
    profileImage: Int, // 假设是资源 ID
    message: String,
    dateTime: String
) {
    Row(modifier = Modifier.padding(4.dp)) {
        Image(
            painter = painterResource(id = profileImage),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape) // 圆形裁剪
        )
        Column(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f)
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier
                    .background(ReceivedMessageBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .fillMaxWidth(0.75f)
            )
            Text(
                text = dateTime,
                color = SecondaryText,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun SentMessageItem(message: String, dateTime: String) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .background(Primary, RoundedCornerShape(8.dp)) // 应用发送消息的背景样式
            .padding(12.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = dateTime,
            color = SecondaryText,
            fontSize = 10.sp
        )
    }
}
