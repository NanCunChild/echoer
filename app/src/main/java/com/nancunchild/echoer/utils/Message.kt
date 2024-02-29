package com.nancunchild.echoer.utils

data class Message(
    val id: String, // 消息的唯一标识符
    val content: String, // 消息内容
    val author: String, // 发送者标识（例如用户名）
    val timestamp: String,//时间
    val isSentByMe: Boolean, // 标识消息是否由当前用户发送
    val avatarUrl: Int // 发送者头像的本地路径或URL
)

