package com.example.echoer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.echoer.databinding.ActivityChatBinding;
import com.example.echoer.models.ChatMessage;
import com.example.echoer.adapters.ChatMessageAdapter;
import com.example.echoer.utilities.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private List<ChatMessage> chatMessages;
    private ChatMessageAdapter chatAdapter;

    // 声明蓝牙相关的成员变量
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    // 声明用于处理蓝牙消息的Handler
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setListeners();
        init();

        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("DEVICE_NAME");
        String deviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
        // 使用这些数据进行相关操作

        // 初始化蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 获取蓝牙设备对象
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        // 建立蓝牙连接
        connectDevice();
    }

    private void init() {
        // 配置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.chatRecycleView.setLayoutManager(manager);

        // 初始化 mHandler
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> finish());

        binding.send.setOnClickListener(v -> {
            String message = binding.inputText.getText().toString(); // 获取消息文本框内容
            // 将消息添加到消息列表里
            if (!message.equals("")) {
                chatAdapter.notifyDataSetChanged(); // 通知适配器数据发生了变化
                sendMessage(message); // 发送蓝牙消息
                binding.inputText.setText(""); // 清空输入框
            }
            Toast.makeText(this, "消息已发送", Toast.LENGTH_SHORT).show();
        });
    }

    // 发送蓝牙消息
    private void sendMessage(String message) {
        byte[] buffer = message.getBytes();
        try {
            if (outputStream != null) {
                try {
                    outputStream.write(buffer);
                    mHandler.obtainMessage(Constants.VIEW_TYPE_RECEIVED, -1, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 输出流为空，蓝牙连接未建立
                Toast.makeText(this, "蓝牙连接未建立", Toast.LENGTH_SHORT).show();
            }
            outputStream.write(buffer);
            mHandler.obtainMessage(Constants.VIEW_TYPE_RECEIVED, -1, -1, buffer).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 建立蓝牙连接
    private void connectDevice() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 标准串口服务的UUID
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 启动线程用于读取蓝牙消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                while (true) {
                    try {
                        bytes = inputStream.read(buffer);
                        mHandler.obtainMessage(Constants.VIEW_TYPE_RECEIVED, bytes, -1, buffer).sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }
}
