package com.example.echoer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothScanner;
    private ScanCallback scanCallback;
    private List<BluetoothDevice> scannedDevices = new ArrayList<>();
    private LinearLayout deviceListLayout;
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
        System.out.println("Ready to require permissions...");
        Log.d("debug","Ready to require permissions...");

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();

            // 初始化 ScanCallback
            initScanCallback();

            // 获取 LinearLayout 容器
            deviceListLayout = findViewById(R.id.deviceListLayout);

            // 开始蓝牙设备扫描
            startScanning();

        }
    }

    private void renderDeviceList() {
        // 清空原有的设备列表
        deviceListLayout.removeAllViews();

        // 根据扫描到的设备列表创建 TextView 并添加到 LinearLayout
        for (BluetoothDevice device : scannedDevices) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText("Device Name: " + device.getName() + "\nDevice Address: " + device.getAddress());

            deviceListLayout.addView(textView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter bluetoothFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(NetworkBroadcastReceiver.getBluetoothStateReceiver(), bluetoothFilter);
        registerReceiver(NetworkBroadcastReceiver.getWifiStateReceiver(), wifiFilter);
    }


    private void startScanning() {
        if (bluetoothScanner != null) {
            bluetoothScanner.startScan(scanCallback);

            // 扫描一段时间后停止扫描
            Handler handler = new Handler();
            handler.postDelayed(this::stopScanning, 5000); // 扫描5秒后停止
        }
    }

    private void stopScanning() {
        if (bluetoothScanner != null) {
            bluetoothScanner.stopScan(scanCallback);
        }
    }

    private void initScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                // 获取扫描到的蓝牙设备
                BluetoothDevice device = result.getDevice();

                // 检查设备是否已经在列表中
                if (!scannedDevices.contains(device)) {
                    scannedDevices.add(device);

                    // 在 UI 线程更新设备列表
                    runOnUiThread(() -> renderDeviceList());
                }
            }
        };
    }

}