package com.example.echoer;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class UIElementsManager {
    //  该类获取蓝牙扫描界面的所有显示元素，封装赋值。
    //  注意，该类应该而且只能为静态调用，任何实例化都是非法的。
    private static TextView bluetoothState;
    private static TextView wifiState;
    private static LinearLayout devicesDetectedList;
    private static WeakReference<View> mRootViewRef;
    private UIElementsManager(){}
    public static void initialize(View rootView){
        if (rootView!=null) mRootViewRef = new WeakReference<>(rootView);;
        bluetoothState = rootView.findViewById(R.id.bluetoothStatus);
        wifiState = rootView.findViewById(R.id.wifiStatus);
        devicesDetectedList = rootView.findViewById(R.id.deviceListLayout);
    }

//    Set & Get 经典方法，但是在这里没什么用，先留着，说不定之后要用
//    public void setBluetoothState(TextView bluetoothState) {
//        this.bluetoothState = bluetoothState;
//    }
//
//    public void setWifiState(TextView wifiState) {
//        this.wifiState = wifiState;
//    }

//    public TextView getWifiState() {
//        return wifiState;
//    }
//
//    public TextView getBluetoothState() {
//        return bluetoothState;
//    }

    public static void setBluetoothStateText(String text){
        bluetoothState.setText(text);
    }
    public static void setWifiStateText(String text){
        wifiState.setText(text);
    }
}
