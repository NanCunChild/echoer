package com.example.echoer.utilities;

public class BluetoothScanResultMaker {
    private final String deviceName;
    private final String deviceAddress;

    public BluetoothScanResultMaker(String deviceName, String deviceAddress){
        this.deviceName=deviceName;
        this.deviceAddress=deviceAddress;
    }

    public String getDeviceName(){
        return deviceName;
    }

    public String getDeviceAddress(){
        return deviceAddress;
    }
}