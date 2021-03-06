package com.iorbit_tech.healthcare.caretakerapp.ble;

import java.io.Serializable;

public class BeaconInfo implements Serializable {
    public String name;
    public int rssi;
    public String distance;
    public String distanceDesc;
    public int major;
    public int minor;
    public boolean isConnected;
    public int txPower;
    public String mac;
    public String uuid;
    public int batteryPower;
    public int version;
    public String scanRecord;
    public String threeAxis;
    public String temp;
    public String humidity;


    @Override
    public String toString() {
        return "BeaconInfo{" +
                "name='" + name + '\'' +
                ", rssi=" + rssi +
                ", distance='" + distance + '\'' +
                ", distanceDesc='" + distanceDesc + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", isConnected=" + isConnected +
                ", txPower=" + txPower +
                ", mac='" + mac + '\'' +
                ", uuid='" + uuid + '\'' +
                ", batteryPower=" + batteryPower +
                ", version=" + version +
                ", scanRecord='" + scanRecord + '\'' +
                ", threeAxis='" + threeAxis + '\'' +
                ", temp='" + temp + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }
}