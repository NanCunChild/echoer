package com.example.echoer.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class BC_ScanAdapter {
    private final BluetoothAdapter bluetoothAdapter;  // BluetoothAdapter 实例，用于管理蓝牙功能
    private final Context context;                    // 上下文，用于注册和注销BroadcastReceiver
    private final ScanCallback scanCallback;          // 回调接口实例，用于处理找到的蓝牙设备

    // 构造函数
    public BC_ScanAdapter(Context context, ScanCallback scanCallback) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 获取默认蓝牙适配器
        this.context = context;
        this.scanCallback = scanCallback;
    }

    // 开始扫描的方法
    private void startScan() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }

        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();  // 开始蓝牙发现过程
        }else {
            return;
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);  // 创建一个IntentFilter来捕获蓝牙设备发现事件
        context.registerReceiver(receiver, filter);  // 注册BroadcastReceiver以接收蓝牙设备
    }

    // 停止扫描的方法
    @SuppressLint("MissingPermission")
    private void stopScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();  // 取消蓝牙发现过程
        }
        context.unregisterReceiver(receiver);  // 注销BroadcastReceiver
    }

    public void scanOptimizer(boolean mode){
        if(mode){
            startScan();
        }else {
            stopScan();
        }
    }

    // BroadcastReceiver，用于接收蓝牙设备发现事件
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  // 获取找到的蓝牙设备
//                Log.d("Bluetooth", String.valueOf(device));
                scanCallback.onDeviceFound(device);  // 调用回调方法处理找到的设备
            }
        }
    };

    // 回调接口，供外部实现，用于处理找到的蓝牙设备
    public interface ScanCallback {
        void onDeviceFound(BluetoothDevice device);
    }
}