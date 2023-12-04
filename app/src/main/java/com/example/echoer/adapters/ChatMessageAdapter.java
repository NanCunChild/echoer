package com.example.echoer.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
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
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {    // 处理由对方发出来的信息
            return new ReceivedMessageViewHolder(
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
        ChatMessage chatMessage = chatMessageList.get(position);
        if (getItemViewType(position) == Constants.VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessage);
            // 如果消息包含图片，设置ImageView可见并加载图片
//            if (chatMessage.getImageUrl() != null && !chatMessage.getImageUrl().isEmpty()) {
//                ((SentMassageViewHolder) holder).binding.imageMessage.setVisibility(View.VISIBLE);
//                // 使用Glide或其他库加载图片
//                // Glide.with(holder.itemView.getContext()).load(chatMessage.getImageUrl()).into(((SentMassageViewHolder) holder).binding.imageView);
//            } else {
//                ((SentMassageViewHolder) holder).binding.imageMessage.setVisibility(View.GONE);
//            }
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessage);
            // 同样的逻辑适用于接收的消息
            // ...
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
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.textMessage.setText(chatMessage.getMessage());

//            if (chatMessage.getImageUrl() != null && !chatMessage.getImageUrl().isEmpty()) {
//                binding.imageMessage.setVisibility(View.VISIBLE);
//                Glide.with(binding.imageMessage.getContext())
//                        .load(chatMessage.getImageUrl())
//                        .into(binding.imageMessage);
//            } else {
//                binding.imageMessage.setVisibility(View.GONE);
//            }
        }
    }


    // 装载收到消息的视图
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.textMessage.setText(chatMessage.getMessage());
        }
    }
}
