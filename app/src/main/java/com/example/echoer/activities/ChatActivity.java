package com.example.echoer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.echoer.databinding.ActivityChatBinding;
import com.example.echoer.models.ChatMessage;
import com.example.echoer.adapters.ChatMessageAdapter;
import com.example.echoer.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private List<ChatMessage> chatMessages;
    private ChatMessageAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initMessageList();
        init();
    }

    private void init() {
        // 配置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.chatRecycleView.setLayoutManager(manager);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatMessageAdapter(chatMessages);
        binding.chatRecycleView.setAdapter(chatAdapter);
    }

    private void initMessageList() {
        chatMessages = new ArrayList<ChatMessage>();
        ChatMessage msg1 = new ChatMessage("20231022", "Hello, World!", "Jobs", Constants.VIEW_TYPE_RECEIVED);
        chatMessages.add(msg1);
        ChatMessage msg2 = new ChatMessage("20231026", "Beep", "Me", Constants.VIEW_TYPE_SENT);
        chatMessages.add(msg2);
        ChatMessage msg3 = new ChatMessage("20231026", "Don't stop me now", "Jobs", Constants.VIEW_TYPE_RECEIVED);
        chatMessages.add(msg3);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> finish());

        String message = binding.inputText.getText().toString();
        binding.send.setOnClickListener(v -> {
            // 将消息添加到消息列表里
            if (!message.equals("")) {
                chatMessages.add(new ChatMessage("20231029", message, "Me", Constants.VIEW_TYPE_SENT));
            }
        });
    }
}
