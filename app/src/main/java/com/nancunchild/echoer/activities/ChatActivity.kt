package com.nancunchild.echoer.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.nancunchild.echoer.fragments.ChatScreen

class ChatActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceAddress = intent.getStringExtra("deviceAddress")
        val deviceName = intent.getStringExtra("deviceName")
        val isDarkMode = intent.getBooleanExtra("isDarkMode", false)

        setContent {
//            Text(text = "This is the second page.")
//            ChatFragment()
            ChatScreen(
                deviceAddress = deviceAddress,
                deviceName = deviceName,
                isDarkMode = isDarkMode,
            )
        }
    }
}