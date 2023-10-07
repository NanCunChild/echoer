package com.example.echoer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements BluetoothStateListener {
    // 定义蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // 定义数组适配器
    private ArrayAdapter<String> mArrayAdapter;
    private static final int PERMISSION_REQUEST_CODE = 1; // 权限请求码
    private final BroadcastReceiver mReceiver = new BluetoothBroadcastReceiver(this);


    private BluetoothBroadcastReceiver bluetoothStateReceiver;

    @Override
    public void onBluetoothStateOn() {
        TextView mbluetoothStatusText = (TextView) findViewById(R.id.bluetoothStatus);
        System.out.println("蓝牙已开启");
        mbluetoothStatusText.setText("蓝牙已开启");
    }

    @Override
    public void onBluetoothStateOff() {
        TextView mbluetoothStatusText = (TextView) findViewById(R.id.bluetoothStatus);
        System.out.println("蓝牙已关闭");
        mbluetoothStatusText.setText("蓝牙已关闭");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Ready to require permissions...");
        onRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销 BroadcastReceiver
        unregisterReceiver(bluetoothStateReceiver);
    }

//    private void checkInitialBluetoothState() {
//        TextView mbluetoothStatusText = (TextView) findViewById(R.id.bluetoothStatus);
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter != null) {
//            // 设备支持蓝牙
//            if (bluetoothAdapter.isEnabled()) {
//                onBluetoothStateOn();  // 蓝牙已启用
//            } else {
//                onBluetoothStateOff(); // 蓝牙已禁用
//            }
//        } else {
//            // 设备不支持蓝牙
//            mbluetoothStatusText.setText("设备不支持蓝牙");
//        }
//    }


    private void blueToothScan() {

        TextView mbluetoothSupportText = (TextView) findViewById(R.id.bluetoothSupport);
        Spinner mDevicesSpinner = (Spinner) findViewById(R.id.devicesSpinner);

        // 实例化数组适配器
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        // 设置适配器到下拉列表
        mDevicesSpinner.setAdapter(mArrayAdapter);
        // 获取默认蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 注册 BroadcastReceiver，可以实时检测蓝牙开关
        bluetoothStateReceiver = new BluetoothBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        // 检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            mbluetoothSupportText.setText("设备不支持蓝牙");
            Toast.makeText(this, "此设备不支持蓝牙", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                onBluetoothStateOn();  // 蓝牙已启用
            } else {
                onBluetoothStateOff(); // 蓝牙已禁用
            }
            mbluetoothSupportText.setText("支持蓝牙");
            // 获取已配对的设备
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // 检查已配对的设备数量
            if (pairedDevices.size() > 0) {
                // 循环遍历并添加设备到适配器
                for (BluetoothDevice device : pairedDevices) {
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    System.out.println(device);
                }
            } else {
                // 没有找到已配对的设备时显示提示
                Toast.makeText(this, "没有配对的设备", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void onRequestPermissions() {
        // 需要注意，用户选择为异步调用，所以该函数只起到第一次检验的作用。用户是否授权需要在 onRequestPermissionsResult 里面进行鉴别。
        List<String> permissionsNeeded = new ArrayList<>();
        // 需要但是未被授予的权限，这样可以将批量授权一次性做完
        String[] permissions = new String[]{
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                // ... 其他需要检查的权限，我之后再添加进来 ...
        };
        // 所有需要的权限，会检查这些权限，没有授权的加入 permissionsNeeded 里面。

        // 逐一检查，没有授权的批量添加至列表中，稍后统一授权
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // 如果权限未被授予，添加到列表中
                permissionsNeeded.add(permission);
            }
        }

        // API 版本兼容处理：特殊的权限调用————
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // 以下都需要API版本在31以上才行
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        System.out.println("Build Version:" + Build.VERSION.SDK_INT);
        System.out.println("Permission Needed:" + permissionsNeeded);

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
            // 如果出现未授权的选项，将逐一授权
        } else {
            System.out.println("All Permissions Granted.");
            // 所有权限都已被授予，执行相关操作
            blueToothScan();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //TODO: 将这里的 PERMISSION_REQUEST_CODE 细分，考虑所有的权限请求，对于用户的拒绝需要使用不同的提示，建议使用 Toast。 --NanCunChild 2023/10/5
            case PERMISSION_REQUEST_CODE: {
                // 如果权限请求被取消，那么数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户授予，执行蓝牙扫描
                    System.out.println("User Granted.");
                    blueToothScan();

                } else {
                    System.out.println("User Rejected.");
                    // 权限被用户拒绝，提示用户授予权限
                    Toast.makeText(this, "需要位置权限来搜索蓝牙设备", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // 处理其他权限请求的情况
        }
    }


}


