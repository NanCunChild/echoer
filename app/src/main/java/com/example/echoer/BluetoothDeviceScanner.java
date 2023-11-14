package com.example.echoer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.echoer.managers.PermissionManager;
import com.example.echoer.managers.UIElementsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BluetoothDeviceScanner {
    private final Activity activity;
    private PermissionManager permissionManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private List<BluetoothDevice> scannedDevices = new ArrayList<>();
    private ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 扫描模式
            .setReportDelay(0) // 报告延迟
            .build();

    public BluetoothDeviceScanner(Activity activity) {
        this.activity = activity;
        String[] permissionNeeded = new String[]{
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        permissionManager = new PermissionManager((AppCompatActivity) activity, permissionNeeded);
        this.permissionManager.setPermissionAuthResultActor(new PermissionManager.PermissionAuthResultActor() {
            // 这里是用户对于授权请求框的行为
            @Override
            public void onPermissionsGranted() {
                Log.d("Permissions","All Permission Granted.");
            }

            @Override
            public void onPermissionsDenied(String[] permissionDenied) {
                Log.w("Permissions","Permission Denied:"+ Arrays.toString(permissionDenied));
            }
        });
        permissionManager.requestPermissions();
        // TODO 1: 此处需要在回调时想一个办法使应用走第二条路，不要再试图获取权限。而是在scroll中显示权限不足无法显示。
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (!bluetoothAdapter.isEnabled()) {

            bluetoothAdapter.enable();
            // TODO 2: 这个可恶的权限检查红线得想办法搞掉。我的建议是完成 Step1 之后在每个函数开头都加一个 permissionManager 引用检查

            // 初始化 ScanCallback
            initScanCallback();

            // 开始蓝牙设备扫描
            startScanning();
        }

    }

    private void startScanning() {
        // 实例化即扫描，不要外部引用这个方法

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        bluetoothLeScanner.startScan(scanCallback);

        // 扫描一段时间后停止扫描
        Handler handler = new Handler();
        handler.postDelayed(this::stopScanning, 5000); // 扫描5秒后停止
    }


    private void stopScanning() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
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

                    // 在 UI 更新设备列表
                    UIElementsManager.clearDeviceList();
                    for (BluetoothDevice deviceItem : scannedDevices) {
                        Log.d("Scan",deviceItem.getName() + device.getAddress());
                        TextView textView = new TextView(activity);
                        textView.setText(deviceItem.getName() + "\n" + deviceItem.getAddress());

                        // 这里你可以设置TextView的样式，例如字体大小、颜色等
                        textView.setTextSize(16);
                        textView.setPadding(8, 8, 8, 8);

                        // 添加TextView到LinearLayout
                        UIElementsManager.addViewToDeviceList(textView);
                    }
                }
            }
        };
    }
}

