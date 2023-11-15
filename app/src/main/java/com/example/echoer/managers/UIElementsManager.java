package com.example.echoer.managers;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.echoer.R;

import java.lang.ref.WeakReference;

public class UIElementsManager {
    //  该类获取蓝牙扫描界面的所有显示元素，封装赋值。
    //  注意，该类应该而且只能为静态调用，任何实例化都是非法的。
    private static TextView bluetoothState;
    private static TextView wifiState;
    private static ListView devicesDetectedList;
    private static Button startScan;
    private static WeakReference<View> mRootViewRef;

    private UIElementsManager() {
    }

    public static void initialize(View rootView) {
        if (rootView != null) mRootViewRef = new WeakReference<>(rootView);
        bluetoothState = rootView.findViewById(R.id.bluetoothStatus);
        wifiState = rootView.findViewById(R.id.wifiStatus);
        devicesDetectedList = rootView.findViewById(R.id.deviceListLayout);
        startScan = rootView.findViewById(R.id.btm_startScan);
    }

    public static void setBluetoothStateText(String text) {
        bluetoothState.setText(text);
    }

    public static void setWifiStateText(String text) {
        wifiState.setText(text);
    }

    public static void clearDeviceList() {
        devicesDetectedList.removeAllViews();
    }

    public static void refreshDeviceList(ArrayAdapter<String> arrayAdapter) {
        devicesDetectedList.setAdapter(arrayAdapter);
    }

    public static void setScanButtonText(String text) {
        startScan.setText(text);
    }
    public static void setButtonStatus(boolean isEnabled){
        startScan.setEnabled(isEnabled);
    }
}
