package com.example.echoer.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.echoer.NetworkBroadcastReceiver;
import com.example.echoer.adapters.BC_ScanAdapter;
import com.example.echoer.managers.PermissionManager;
import com.example.echoer.R;
import com.example.echoer.managers.UIElementsManager;
import com.example.echoer.utilities.BluetoothScanResultMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean isScanning = false;
    private PermissionManager permissionManager;
    private BC_ScanAdapter bluetoothAdapter;
    private List<String> scannedDeviceListUI = new ArrayList<>();
    private ListView deviceListView;
    private List<BluetoothScanResultMaker> scannedDeviceList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIElementsManager.initialize(findViewById(android.R.id.content));
        scannedDeviceListUI = new ArrayList<>();

        ////////////////////////权限请求/////////////////////////
        // 初始化PermissionManager
        String[] permissions = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
                // ... 其他需要的权限 ...
        };
        permissionManager = new PermissionManager(this, permissions);
        permissionManager.setPermissionAuthResultActor(new PermissionManager.PermissionAuthResultActor() {
            // 这里是用户对于授权请求框的行为
            @Override
            public void onPermissionsGranted() {
                Log.d("Permissions", "All Permission Granted.");
            }

            @Override
            public void onPermissionsDenied(String[] permissionDenied) {
                Log.w("Permissions", "Permission Denied:" + Arrays.toString(permissionDenied));
            }
        });
        permissionManager.requestPermissions();
        ////////////////////////权限请求/////////////////////////

        NetworkBroadcastReceiver.getBluetoothStateReceiverInitial(); // 有点丑陋，不过我认为是谷歌的锅

        this.bluetoothAdapter = new BC_ScanAdapter(this, new BC_ScanAdapter.ScanCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                // 在这里处理找到的蓝牙设备
                String deviceName = device.getName() == null ? "未知设备" : device.getName();
                String deviceAddress = device.getAddress();
                Log.d("Bluetooth", "Found device: " + deviceName + " - " + deviceAddress);
                BluetoothScanResultMaker deviceItem = new BluetoothScanResultMaker(deviceName, deviceAddress);
                scannedDeviceList.add(deviceItem);
                scannedDeviceListUI.add(deviceName + "-" + deviceAddress);
                ArrayAdapter<String> scannedDeviceArray = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, scannedDeviceListUI);
                UIElementsManager.refreshDeviceList(scannedDeviceArray);
            }
        });

        Button startScan = findViewById(R.id.btm_startScan); // 开始扫描按钮
        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bluetoothAdapter.scanOptimizer(!isScanning);
                    isScanning = !isScanning;
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }

                if (isScanning) {
                    Log.d("Bluetooth", scannedDeviceList.toString());
                    UIElementsManager.setScanButtonText("停止扫描");
                } else {
                    scannedDeviceList = new ArrayList<>(); // 扫描结果清零，防止堆一堆没用的设备
                    UIElementsManager.setScanButtonText("开始扫描");
                }

            }
        });

        deviceListView = findViewById(R.id.deviceListLayout);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.scanOptimizer(false);
                BluetoothScanResultMaker selectedResult = scannedDeviceList.get(position);
                openChatActivity(selectedResult);
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void openChatActivity(BluetoothScanResultMaker selectedResult) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("DEVICE_NAME", selectedResult.getDeviceName());
        intent.putExtra("DEVICE_ADDRESS", selectedResult.getDeviceAddress());
        // 根据需要添加更多参数

        startActivity(intent);
    }


    protected void onPause() {
        super.onPause();
        try {
            bluetoothAdapter.scanOptimizer(false);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothAdapter.scanOptimizer(false);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter bluetoothFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(NetworkBroadcastReceiver.getBluetoothStateReceiver(), bluetoothFilter);
        registerReceiver(NetworkBroadcastReceiver.getWifiStateReceiver(), wifiFilter);
        permissionManager.requestPermissions();
    }
}