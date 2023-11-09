package com.example.echoer.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.echoer.databinding.ItemContainerReceivedMessageBinding;
import com.example.echoer.databinding.ItemContainerSentMessageBinding;
import com.example.echoer.models.ChatMessage;
import com.example.echoer.utilities.Constants;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 信息列表
    private final List<ChatMessage> chatMessageList;

    public ChatMessageAdapter(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("tag", "why!!!!!!");
        if (viewType == Constants.VIEW_TYPE_SENT) {   // 处理由“我”发出来的信息
            return new SentMassageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {    // 处理由对方发出来的信息
            return new ReceivedMassageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Constants.VIEW_TYPE_SENT) {
            Log.d("adapter", (chatMessageList.get(position)).getMessage());
            ((SentMassageViewHolder) holder).setData((chatMessageList.get(position)));
        } else {
            ((ReceivedMassageViewHolder) holder).setData(chatMessageList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessageList.get(position).getIsSent();
    }

    // 装载发送消息的视图
    static class SentMassageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMassageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.textMessage.setText(chatMessage.getMessage());
        }
    }

    // 装载收到消息的视图
    static class ReceivedMassageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMassageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.textMessage.setText(chatMessage.getMessage());
        }
    }
}
