package com.example.echoer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private UIElementsManager uiManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothScanner;
    private ScanCallback scanCallback;
    private List<BluetoothDevice> scannedDevices = new ArrayList<>();
    private LinearLayout deviceListLayout;
    private ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 扫描模式
            .setReportDelay(0) // 报告延迟
            .build();
    private final android.content.BroadcastReceiver bluetoothStateReceiver = new android.content.BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (bluetoothState == BluetoothAdapter.STATE_ON) {
                    System.out.println("蓝牙已开启");
                    uiManager.setBluetoothStateText("蓝牙已开启");
                } else if (bluetoothState == BluetoothAdapter.STATE_OFF) {
                    System.out.println("蓝牙已关闭");
                    uiManager.setBluetoothStateText("蓝牙已关闭");
                } else if (bluetoothState == BluetoothAdapter.STATE_CONNECTED) {
                    System.out.println("蓝牙已连接至其它设备");
                    uiManager.setBluetoothStateText("蓝牙已连接至其它设备");
                } else if (bluetoothState == BluetoothAdapter.STATE_TURNING_OFF) {
                    System.out.println("蓝牙正在关闭");
                    uiManager.setBluetoothStateText("蓝牙正在关闭...");
                } else if (bluetoothState == BluetoothAdapter.STATE_TURNING_ON) {
                    System.out.println("蓝牙正在开启");
                    uiManager.setBluetoothStateText("蓝牙正在开启...");
                } else if (bluetoothState == BluetoothAdapter.STATE_CONNECTING) {
                    System.out.println("蓝牙正在连接");
                }
            }
        }
    };
    private final android.content.BroadcastReceiver wifiStateReceiver = new android.content.BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        System.out.println("Wi-Fi 已关闭");
                        uiManager.setWifiStateText("Wi-Fi 已关闭");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        System.out.println("Wi-Fi 已开启");
                        uiManager.setWifiStateText("Wi-Fi 已开启");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        System.out.println("Wi-Fi 正在关闭");
                        uiManager.setWifiStateText("Wi-Fi 正在关闭");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        System.out.println("Wi-Fi 正在开启");
                        uiManager.setWifiStateText("Wi-Fi 正在开启");
                        break;
                }
            }
        }
    };

    public android.content.BroadcastReceiver getBluetoothStateReceiver() {
        return bluetoothStateReceiver;
    }

    public android.content.BroadcastReceiver getWifiStateReceiver() {
        return wifiStateReceiver;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiManager = new UIElementsManager(findViewById(android.R.id.content));
        System.out.println("Ready to require permissions...");
        if (!checkPermissions(true)) {
                    new AlertDialog.Builder(this)
                .setTitle("权限请求")
                .setMessage("用户未能接受权限请求，请在设置中开启相关权限后启动")
                .setPositiveButton("我明白", (dialog, which) -> {
                    finish();
                })
                .show();
        }else{
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
        registerReceiver(getBluetoothStateReceiver(), bluetoothFilter);
        registerReceiver(getWifiStateReceiver(), wifiFilter);
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



    private boolean checkPermissions(boolean claimPermission) {
        List<String> permissionNeeded = new ArrayList<>();
        List<String> permissionRefused = new ArrayList<>();
        String[] permissionRequest = new String[]{
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                // ... 其他需要检查的权限，我之后再添加进来 ...
        };

        for (String permission : permissionRequest) { // 逐一检查，没有授权的批量添加至列表中，稍后统一授权
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(permission)) {// 之前的权限被拒绝过
                    permissionRefused.add(permission);
                } else {// 权限从未请求过
                    permissionNeeded.add(permission);
                }
            }
        }

        // API 版本兼容处理：特殊的权限调用————
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // 以下都需要API版本在31以上才行
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {
                    permissionRefused.add(Manifest.permission.BLUETOOTH_SCAN);
                } else {
                    permissionNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
                    permissionRefused.add(Manifest.permission.BLUETOOTH_CONNECT);
                } else {
                    permissionNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
                }
            }
        }

        System.out.println("Build Version:" + Build.VERSION.SDK_INT);
        System.out.println("Permission Needed:" + permissionNeeded);
        System.out.println("Permission Refused:" + permissionRefused);

        if (!permissionNeeded.isEmpty() && claimPermission) {
            ActivityCompat.requestPermissions(this,
                    permissionNeeded.toArray(new String[0]),
                    0);
            // 如果出现未授权的选项，将逐一授权
        } else if (permissionNeeded.isEmpty()) {
            System.out.println("All Permissions Granted.");
            return true;
        }
        return false;
    }

//    private void permissionExplanationDialog(String permission) {
//        String message = "我们需要以下权限来确保应用正常工作: " + permission;
//        new AlertDialog.Builder(this)
//                .setTitle("权限请求")
//                .setMessage(message)
//                .setPositiveButton("我明白", (dialog, which) -> {
//                    requestPermissionLauncher.launch(permission);
//                })
//                .setNegativeButton("取消", null)
//                .show();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //TODO: 将这里的 PERMISSION_REQUEST_CODE 细分，考虑所有的权限请求，对于用户的拒绝需要使用不同的提示，建议使用 Toast。 --NanCunChild 2023/10/5
            case 0: {
                // 如果权限请求被取消，那么数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {// 权限被用户授予，执行蓝牙扫描
                    System.out.println("onRequestPermissionsResult: User Granted.");
                    this.startScanning();

                } else {// 权限被用户拒绝，提示用户授予权限
                    System.out.println("onRequestPermissionsResult: User Rejected.");
                    Toast.makeText(this, "需要位置权限来搜索蓝牙设备", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // 处理其他权限请求的情况
        }
    }
}