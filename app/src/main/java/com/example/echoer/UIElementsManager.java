package com.example.echoer;

import android.view.View;
import android.widget.TextView;

public class UIElementsManager { //该类获取蓝牙扫描界面的所有显示元素，封装赋值
    private TextView bluetoothState;
    private TextView wifiState;
    public UIElementsManager(View rootView){
        bluetoothState = rootView.findViewById(R.id.bluetoothStatus);
        wifiState = rootView.findViewById(R.id.wifiStatus);
    }

    public void setBluetoothState(TextView bluetoothState) {
        this.bluetoothState = bluetoothState;
    }

    public void setWifiState(TextView wifiState) {
        this.wifiState = wifiState;
    }

    public TextView getWifiState() {
        return wifiState;
    }

    public TextView getBluetoothState() {
        return bluetoothState;
    }

    public void setBluetoothStateText(String text){
        bluetoothState.setText(text);
    }
    public void setWifiStateText(String text){
        wifiState.setText(text);
    }
}
