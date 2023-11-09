package com.example.echoer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.echoer.BluetoothDeviceScanner;
import com.example.echoer.NetworkBroadcastReceiver;
import com.example.echoer.managers.PermissionManager;
import com.example.echoer.R;
import com.example.echoer.managers.UIElementsManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BluetoothDeviceScanner bluetoothDeviceScanner;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothScanner;
    private List<BluetoothDevice> scannedDevices = new ArrayList<>();
    private PermissionManager permissionManager;
    private ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 扫描模式
            .setReportDelay(0) // 报告延迟
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIElementsManager.initialize(findViewById(android.R.id.content));
        Log.d("Permissions", "Ready to require permissions...");

        // 初始化PermissionManager
        String[] permissions = {
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                // ... 其他需要的权限 ...
        };
        permissionManager = new PermissionManager(this, permissions);
        permissionManager.setPermissionCallback(new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionsGranted() {
                // 当所有权限都被授权时执行的操作
                Toast.makeText(MainActivity.this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                // 当某些权限被拒绝时执行的操作
                Toast.makeText(MainActivity.this, "Permissions denied: " + deniedPermissions, Toast.LENGTH_SHORT).show();
            }
        });
        permissionManager.requestPermissions();
        Log.d("Permissions", "Permission request finished.");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothDeviceScanner = new BluetoothDeviceScanner(this);

        // 以下的代码极为丑陋，要不是因为蓝牙广播是非Sticky，我就不用这样了。
        // TODO : 将initial蓝牙检测放进广播类中变成一个方法。
        if(bluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) UIElementsManager.setBluetoothStateText("蓝牙已开启");
        else UIElementsManager.setBluetoothStateText("蓝牙已关闭");

        // 去往聊天界面
        Button goToChat = findViewById(R.id.btn_goToChat);
        goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void renderDeviceList() {
        // 清空原有的设备列表
        UIElementsManager.clearDeviceList();

        // 根据扫描到的设备列表创建 TextView 并添加到 LinearLayout
        for (BluetoothDevice device : scannedDevices) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText("Device Name: " + device.getName() + "\nDevice Address: " + device.getAddress());
            UIElementsManager.addViewToDeviceList(textView);
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