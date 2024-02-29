package com.nancunchild.echoer.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nancunchild.echoer.fragments.ChatFragment
import com.nancunchild.echoer.fragments.ChatScreen

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            Text(text = "This is the second page.")
//            ChatFragment()
            ChatScreen()
        }
    }
}

