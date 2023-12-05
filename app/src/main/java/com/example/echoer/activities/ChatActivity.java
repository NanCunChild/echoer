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
import android.widget.TextView;
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
import java.util.Set;
import java.util.UUID;

import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private List<ChatMessage> chatMessages;
    private ChatMessageAdapter chatAdapter;

    // 声明蓝牙相关的成员变量
    private BluetoothAdapter bluetoothAdapter;
    //    private BluetoothDevice bluetoothDevice;
    private BluetoothDevice esp32Device;
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

        init();
        setListeners();

        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("DEVICE_NAME");
        String deviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
        // 使用这些数据进行相关操作

        if (deviceName == null && deviceAddress == null) {
            Toast.makeText(this, "您还没有连接任何蓝牙设备", Toast.LENGTH_SHORT).show();
        } else {
            binding.textName.setText(deviceName);
//            binding.textName.setText(deviceAddress);

            // 初始化蓝牙适配器
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // 获取蓝牙设备对象
//            bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);d
            // 建立蓝牙连接
//            connectDevice();

            // 搜索并连接到ESP32
            connectToESP32(deviceName);
        }
    }

    private void init() {
        // 配置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.chatRecycleView.setLayoutManager(manager);

        // 初始化 mHandler
        mHandler = new Handler(Looper.getMainLooper());

        // 初始化chatMessages, chatAdapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatMessageAdapter(chatMessages);
        binding.chatRecycleView.setAdapter(chatAdapter);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> finish());

        binding.send.setOnClickListener(v -> {
            String message = binding.inputText.getText().toString(); // 获取消息文本框内容
            // 将消息添加到消息列表里
            if (!message.equals("")) {
                // 添加您的消息到聊天界面
                String sendTime = getSendTime();
                chatMessages.add(new ChatMessage(sendTime, message, "Me", Constants.VIEW_TYPE_SENT));
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() - 1);
                binding.inputText.setText(""); // 清空输入框

                sendMessage(message); // 发送蓝牙消息
            }
            Toast.makeText(this, "消息已发送", Toast.LENGTH_SHORT).show();
        });
    }

    private static String getSendTime() {
        // 获取当前日期和时间
        Date currentDate = new Date();

        // 创建日期格式化对象，指定日期和时间的格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 使用格式化对象将日期和时间格式化为字符串
        String formattedDate = dateFormat.format(currentDate);

        return formattedDate;
    }

    // 发送蓝牙消息
    private void sendMessage(String message) {
//        byte[] buffer = message.getBytes();
//        try {
//            if (outputStream != null) {
//                try {
//                    outputStream.write(buffer);
//                    mHandler.obtainMessage(Constants.VIEW_TYPE_RECEIVED, -1, -1, buffer).sendToTarget();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                // 输出流为空，蓝牙连接未建立
//                Toast.makeText(this, "蓝牙连接未建立", Toast.LENGTH_SHORT).show();
//            }
//            outputStream.write(buffer);
//            mHandler.obtainMessage(Constants.VIEW_TYPE_RECEIVED, -1, -1, buffer).sendToTarget();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 接受蓝牙消息
    private void receiveData() {
        try {
            inputStream = bluetoothSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                // 读取数据
                bytes = inputStream.read(buffer);
                String receivedMessage = new String(buffer, 0, bytes);

                // 更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 添加您的消息到聊天界面
                        String sendTime = getSendTime();
                        chatMessages.add(new ChatMessage(sendTime, receivedMessage, "Me", Constants.VIEW_TYPE_RECEIVED));
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() - 1);
                        binding.inputText.setText(""); // 清空输入框
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToESP32(String deviceName) {
        // 搜索配对设备
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(deviceName)) {
                    esp32Device = device;
                    break;
                }
            }
        }

        // 创建并连接socket
        try {
            bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); // 通用串行端口服务UUID
            bluetoothSocket.connect();

            // 获取输出流
            outputStream = bluetoothSocket.getOutputStream();

            // 发送数据
            sendMessage("Hello World");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveData();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 与其他设备建立蓝牙连接
//    private void connectDevice() {
//        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 标准串口服务的UUID
//        try {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
//            bluetoothSocket.connect();
//            inputStream = bluetoothSocket.getInputStream();
//            outputStream = bluetoothSocket.getOutputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 启动线程用于读取蓝牙消息
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                byte[] buffer = new byte[1024];
//                int bytes;
//
//                while (true) {
//                    try {
//                        bytes = inputStream.read(buffer);
//                        mHandler.obtainMessage(Constants.VIEW_TYPE_RECEIVED, bytes, -1, buffer).sendToTarget();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }
//        }).start();
//    }
}
