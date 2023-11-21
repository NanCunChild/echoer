package com.example.echoer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.echoer.R;
import com.example.echoer.databinding.ItemContainerReceivedMessageBinding;
import com.example.echoer.databinding.ItemContainerSentMessageBinding;
import com.example.echoer.models.ChatMessage;
import com.example.echoer.utilities.Constants;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessageList;

    public ChatMessageAdapter(List<ChatMessage> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d("tag", "why!!!!!!");

        if (viewType == Constants.VIEW_TYPE_SENT) {
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

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);
//        return new testViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Constants.VIEW_TYPE_SENT) {
            ((SentMassageViewHolder) holder).setData((chatMessageList.get(position)));
        } else {
            ((ReceivedMassageViewHolder) holder).setData(chatMessageList.get(position));
        }
//        ((testViewHolder) holder).test.setText(chatMessageList.get(position).getMessage());
    }

    static class testViewHolder extends RecyclerView.ViewHolder {
        TextView test;
        public testViewHolder(@NonNull View itemView) {
            super(itemView);
            test = itemView.findViewById(R.id.textMessage);
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
