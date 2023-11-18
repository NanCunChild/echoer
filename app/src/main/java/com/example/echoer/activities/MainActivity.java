package com.example.echoer.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
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
import com.example.echoer.managers.PermissionManager;
import com.example.echoer.R;
import com.example.echoer.managers.UIElementsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean isScanning = false;
    private PermissionManager permissionManager;
    private ListView deviceListView;
    private List<ScanResult> scanResultList;

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // 处理单个扫描结果
            Log.d("BluetoothScan", "Single Scan:" + result);
            Log.d("BluetoothScan","getDevice: "+result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // 处理一批扫描结果
            List<String> deviceInfoList = new ArrayList<>();
            scanResultList = new ArrayList<>();

            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                @SuppressLint("MissingPermission") String deviceName = device.getName() != null ? device.getName() : "Unknown Device";
                String deviceAddress = device.getAddress();
                String deviceInfo = deviceName + " - " + deviceAddress;
                deviceInfoList.add(deviceInfo);
                scanResultList.add(result);
                Log.d("BluetoothScan", "Scan Result: " + result);
                Log.d("BluetoothScan", "Device Name:" + deviceName + "; Device Address:" + deviceAddress);
            }

            Log.d("BluetoothScan", "Batch Scan:" + results);

            // 创建ArrayAdapter并传递给UIElementsManager
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, deviceInfoList);
            deviceListView.setAdapter(adapter);
            UIElementsManager.refreshDeviceList(adapter);
        }


        @Override
        public void onScanFailed(int errorCode) {
            // 处理扫描失败的情况
            Log.d("BluetoothScan", "Scan Failed:" + errorCode);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIElementsManager.initialize(findViewById(android.R.id.content));

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

        NetworkBroadcastReceiver.getBluetoothStateReceiverInitial();

        Button goToChat = findViewById(R.id.btn_goToChat);// 去往聊天界面
        Button startScan = findViewById(R.id.btm_startScan); // 开始扫描
        goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothScan(!isScanning);
            }
        });

        deviceListView = findViewById(R.id.deviceListLayout);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult selectedResult = scanResultList.get(position); // deviceList是ScanResult列表
                openChatActivity(selectedResult);
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void openChatActivity(ScanResult selectedResult) {
        Intent intent = new Intent(this, ChatActivity.class); // 替换ChatActivity为你的聊天页面Activity

        BluetoothDevice device = selectedResult.getDevice();
        intent.putExtra("DEVICE_NAME", device.getName());
        intent.putExtra("DEVICE_ADDRESS", device.getAddress());
        // 你可以根据需要添加更多参数

        startActivity(intent);
    }


    protected void onPause() {
        super.onPause();
        bluetoothScan(false);
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

    @SuppressLint("MissingPermission")
    private void bluetoothScan(boolean sig) {
//        String[] preconditions = new String[]{
//                Manifest.permission.BLUETOOTH,
//                Manifest.permission.BLUETOOTH_SCAN,
//        };
//        PermissionManager tempPer = new PermissionManager(this, preconditions);
//        tempPer.setPermissionAuthResultActor(new PermissionManager.PermissionAuthResultActor() {
//            // 这里是用户对于授权请求框的行为
//            @Override
//            public void onPermissionsGranted() {
//                Log.d("Permissions", "All Permission Granted.");
//            }
//
//            @Override
//            public void onPermissionsDenied(String[] permissionDenied) {
//                Log.w("Permissions", "Permission Denied:" + Arrays.toString(permissionDenied));
//            }
//        });
//        if (tempPer.hasPermissions().length != 0) {
//            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
//            return;
//        }
        BluetoothLeScanner scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)  // 设置为1秒（1000毫秒）
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        if (sig) {
            scanner.startScan(null,settings,scanCallback);
            UIElementsManager.setScanButtonText("停止扫描");
            this.isScanning = true;
        } else {
            scanner.stopScan(scanCallback);
            UIElementsManager.setScanButtonText("开始扫描");
            this.isScanning = false;
        }

    }
}