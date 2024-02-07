package com.example.echoer.utils

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataPacker {

    /**
     * 封装数据成JSON格式。
     * @param message 发送的消息内容。
     * @param senderId 发送者的ID。
     * @param receiverId 接收者的ID。
     * @param timestamp 发送消息的时间戳（如果未提供，则使用当前时间）。
     * @return JSON格式的字符串。
     */
    fun pack(message: String, senderId: String, receiverId: String, timestamp: Long = System.currentTimeMillis()): String {
        val jsonObject = JSONObject().apply {
            put("message", message)
            put("senderId", senderId)
            put("receiverId", receiverId)
            put("timestamp", formatDate(timestamp))
        }
        return jsonObject.toString()
    }

    /**
     * 解析接收到的JSON格式数据。
     * @param jsonData 接收到的JSON字符串。
     * @return 包含解析后数据的Map。
     */
    fun unpack(jsonData: String): Map<String, Any> {
        val jsonObject = JSONObject(jsonData)
        return jsonObject.keys().asSequence().associateWith { jsonObject.get(it) }
    }

    /**
     * 将时间戳格式化为可读的日期时间字符串。
     * @param timestamp 时间戳。
     * @return 格式化的日期时间字符串。
     */
    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}
