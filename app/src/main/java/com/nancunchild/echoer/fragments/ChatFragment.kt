package com.nancunchild.echoer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import com.nancunchild.echoer.R
import com.nancunchild.echoer.theme.ChatScreen
import com.nancunchild.echoer.ui.theme.EchoerTheme

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                EchoerTheme {
                    ChatScreenContent()
                }
            }
        }
    }
}

@Composable
fun ChatScreenContent() {
    Column {
        TopAppBar(
            title = {
                Text(text = "Chat", color = MaterialTheme.colors.onPrimary)
            },
            navigationIcon = {
                IconButton(onClick = { /* Handle back button press */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Handle info button press */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Info",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.primary
        )
        // The rest of your chat UI goes here
        ChatScreen() // This is the function you defined for your chat UI
    }
}
