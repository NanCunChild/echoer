package com.nancunchild.echoer.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nancunchild.echoer.fragments.ChatFragment
import com.nancunchild.echoer.fragments.ChatScreen

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var deviceAddress = intent.getStringExtra("deviceAddress")
        var deviceName = intent.getStringExtra("deviceName")
        var isDarkMode = intent.getBooleanExtra("isDarkMode", false)

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