package com.nancunchild.echoer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.nancunchild.echoer.ui_components.UserInput

class ChatFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // MaterialTheme 可以提供一致的样式和主题
                MaterialTheme {
                    // 设定整个聊天界面的布局
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 这里可以添加其他组件，例如消息列表
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.weight(1f))
                            UserInput(onMessageSent = { /* ... */ })
                        }

                    }
                }
            }
        }
    }
}

