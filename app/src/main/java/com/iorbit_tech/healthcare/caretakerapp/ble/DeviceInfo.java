package com.iorbit_tech.healthcare.caretakerapp.ble;

import android.bluetooth.le.ScanResult;

import java.io.Serializable;


public class DeviceInfo implements Serializable {
    public String name;
    public int rssi;
    public String mac;
    public String scanRecord;
    public ScanResult scanResult;

    @Override
    public String toString() {
        return "BeaconInfo{" +
                "name='" + name + '\'' +
                ", rssi=" + rssi +
                ", mac='" + mac + '\'' +
                ", scanRecord='" + scanRecord + '\'' +
                '}';
    }
}
