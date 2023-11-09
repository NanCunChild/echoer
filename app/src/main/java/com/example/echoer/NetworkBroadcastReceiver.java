package com.example.echoer;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.example.echoer.managers.UIElementsManager;

public class NetworkBroadcastReceiver {
    private final static android.content.BroadcastReceiver bluetoothStateReceiver = new android.content.BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (bluetoothState == BluetoothAdapter.STATE_ON) {
                    System.out.println("蓝牙已开启");
                    UIElementsManager.setBluetoothStateText("蓝牙已开启");
                } else if (bluetoothState == BluetoothAdapter.STATE_OFF) {
                    System.out.println("蓝牙已关闭");
                    UIElementsManager.setBluetoothStateText("蓝牙已关闭");
                } else if (bluetoothState == BluetoothAdapter.STATE_CONNECTED) {
                    System.out.println("蓝牙已连接至其它设备");
                    UIElementsManager.setBluetoothStateText("蓝牙已连接至其它设备");
                } else if (bluetoothState == BluetoothAdapter.STATE_TURNING_OFF) {
                    System.out.println("蓝牙正在关闭");
                    UIElementsManager.setBluetoothStateText("蓝牙正在关闭...");
                } else if (bluetoothState == BluetoothAdapter.STATE_TURNING_ON) {
                    System.out.println("蓝牙正在开启");
                    UIElementsManager.setBluetoothStateText("蓝牙正在开启...");
                } else if (bluetoothState == BluetoothAdapter.STATE_CONNECTING) {
                    System.out.println("蓝牙正在连接");
                }
            }
        }
    };
    private final static android.content.BroadcastReceiver wifiStateReceiver = new android.content.BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        System.out.println("Wi-Fi 已关闭");
                        UIElementsManager.setWifiStateText("Wi-Fi 已关闭");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        System.out.println("Wi-Fi 已开启");
                        UIElementsManager.setWifiStateText("Wi-Fi 已开启");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        System.out.println("Wi-Fi 正在关闭");
                        UIElementsManager.setWifiStateText("Wi-Fi 正在关闭");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        System.out.println("Wi-Fi 正在开启");
                        UIElementsManager.setWifiStateText("Wi-Fi 正在开启");
                        break;
                }
            }
        }
    };


    public static android.content.BroadcastReceiver getBluetoothStateReceiver() {
        return bluetoothStateReceiver;
    }

    public static android.content.BroadcastReceiver getWifiStateReceiver() {
        return wifiStateReceiver;
    }

}
