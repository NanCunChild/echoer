package com.example.echoer;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcastEventReceiver extends BroadcastReceiver {
    private final UIFlasher listener;

    // 构造函数接收一个 BluetoothStateListener 实例
    public BroadcastEventReceiver(UIFlasher listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        boolean bluetoothState=false;

        if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            System.out.println("Broadcast Triggered.");
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    bluetoothState=false;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    // 蓝牙正在关闭
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                    bluetoothState=true;
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    // 蓝牙正在开启
                    break;
            }
            listener.detectBluetoothState(bluetoothState);
        }
    }
}

