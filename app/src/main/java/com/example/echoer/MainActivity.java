package com.example.echoer;

import android.annotation.SuppressLint;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

public class MainActivity extends Activity {
    // 定义蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // 定义数组适配器
    private ArrayAdapter<String> mArrayAdapter;
    private static final int PERMISSION_REQUEST_CODE = 1; // 权限请求码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView mbluetoothSupportText = (TextView) findViewById(R.id.bluetoothSupport);
        TextView mbluetoothStatusText = (TextView) findViewById(R.id.bluetoothStatus);
        Spinner mDevicesSpinner = (Spinner) findViewById(R.id.devicesSpinner);
        // 实例化数组适配器
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        // 设置适配器到下拉列表
        mDevicesSpinner.setAdapter(mArrayAdapter);

        // 获取默认蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            mbluetoothSupportText.setText("设备不支持蓝牙");
            Toast.makeText(this, "此设备不支持蓝牙", Toast.LENGTH_LONG).show();
            finish();
            return;
        }else{
            mbluetoothSupportText.setText("支持蓝牙");
        }

        onRequestPermissions();

        // 检查蓝牙是否已开启
        if (!mBluetoothAdapter.isEnabled()) {
            mbluetoothStatusText.setText("蓝牙未开启");
        } else {
            mbluetoothStatusText.setText("蓝牙已开启");
            listPairedDevices();  // 蓝牙已开启时列出已配对的设备
        }
    }

    // 列出已配对的蓝牙设备
    private void listPairedDevices() {
        // 获取已配对的设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // 检查已配对的设备数量
        if (pairedDevices.size() > 0) {
            // 循环遍历并添加设备到适配器
            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            // 没有找到已配对的设备时显示提示
            Toast.makeText(this, "没有配对的设备", Toast.LENGTH_LONG).show();
        }
    }

    private void onRequestPermissions() {
        // 需要注意，用户选择为异步调用，所以该函数只起到第一次检验的作用。用户是否授权需要在 onRequestPermissionsResult 里面进行鉴别。
        List<String> permissionsNeeded = new ArrayList<>();
        // 需要但是未被授予的权限，这样可以将批量授权一次性做完
        String[] permissions = new String[]{
                //   Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT,
                // ... 其他需要检查的权限，我之后再添加进来 ...
        };
        // 所有需要的权限，会检查这些权限，没有授权的加入 permissionsNeeded 里面。

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // 如果权限未被授予，添加到列表中
                permissionsNeeded.add(permission);
            }
        }

        // 对于API 23及以上，需要运行时权限，因此在这里使用版本判断，确定是否要使用 ACCESS_FINE_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查位置权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
            //这里的检验也扔到 onRequestPermissionsResult 里面去
        } else {
            System.out.println("All Permissions Granted.");
            // 所有权限都已被授予，执行相关操作
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
                    // 权限被用户授予，执行蓝牙相关操作

                } else {
                    // 权限被用户拒绝，提示用户授予权限
                    Toast.makeText(this, "需要位置权限来搜索蓝牙设备", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // 处理其他权限请求的情况
        }
    }
}
