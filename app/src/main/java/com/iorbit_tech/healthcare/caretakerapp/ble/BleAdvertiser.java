package com.iorbit_tech.healthcare.caretakerapp.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class BleAdvertiser
{
    Activity parent;
    BluetoothManager btManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeAdvertiser leAdvertiser;

    public boolean InitAdvertiser(Activity activity)
    {
        this.parent = activity;
        btManager = (BluetoothManager) parent.getSystemService(Context.BLUETOOTH_SERVICE);
        if(btManager==null) return false;

        bluetoothAdapter = btManager.getAdapter();
        if(bluetoothAdapter==null) return false;

        leAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if(leAdvertiser!=null) { Toast.makeText(parent, "Low energy advertiser enabled", Toast.LENGTH_LONG).show();}
        else  { Toast.makeText(parent, "Low energy sacnner FAILED", Toast.LENGTH_LONG).show();}

        if(leAdvertiser==null) return false;

        return true;
    }

    //A sensor Enroll request is received and reply with sensorID and Name
/* void SensorEnrollReqAcceptAdvt(byte [] payLoad)
 {
	  String sensorName;
	  payLoad[LE_ADVT_OFFSET_ENROLLSTS]= LE_ADVT_SUBCMD_SENSOR_ENROLL_REQ_ACCEPT;
	  if(sensor.sensorName.length()<10)
	  {
		  payLoad[LE_ADVT_OFFSET_SENSORNAME_LEN] =(byte) sensor.sensorName.length();
		  sensorName = sensor.sensorName;
	  }
	  else
	  {
		  sensorName = sensor.sensorName.substring(0, 10);
		  payLoad[LE_ADVT_OFFSET_SENSORNAME_LEN]=10;
	  }
	  System.arraycopy(sensorName.getBytes(), 0, payLoad, LE_ADVT_OFFSET_SENSORNAME, sensorName.length());


 }*/
    boolean advtInProgress =false;
    void StopAdvt()
    {
        leAdvertiser.stopAdvertising(advtCallBack);
        advtInProgress=false;
    }
    //Not Fully Implemnted kept for reference
    void StartAdvertising(byte [] payLoad, int seconds)
    {
        if(advtInProgress)
        {
            try
            {
                advtInProgress=false;
                leAdvertiser.stopAdvertising(advtCallBack);
            }
            catch(Exception exp)
            {

            }
        }

        try {
            AdvertiseSettings.Builder mLeAdvSettingsBuilder =
                    new AdvertiseSettings.Builder().setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
            mLeAdvSettingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
            mLeAdvSettingsBuilder.setConnectable(false);
            mLeAdvSettingsBuilder.setTimeout(seconds * 1000);

            AdvertiseData.Builder mLeAdvDataBuilder = new AdvertiseData.Builder();

            int manufacturerId = 0x0a00;
            // byte [] payLoad2 = new byte[24];
            // for(int i=0;i<14;++i) payLoad2[i]=(byte) i;
            mLeAdvDataBuilder.addManufacturerData(manufacturerId, payLoad);
            advtInProgress = true;
            leAdvertiser.startAdvertising(mLeAdvSettingsBuilder.build(), mLeAdvDataBuilder.build(), advtCallBack);

            Handler off = new Handler();

          /*  off.postDelayed(new Runnable() {

                public void run() {
                    // TODO Auto-generated method stub
                    leAdvertiser.stopAdvertising(advtCallBack);
                    advtInProgress = false;
                }
            }, seconds * 1000);*/
        }
        catch(Exception exp)
        {
            Log.i("Advt",exp.getMessage());
        }

    }

    void ShowMesg(final String mesg)
    {
        parent.runOnUiThread(new Runnable() {

            public void run() {
                // Toast.makeText(parent, mesg, Toast.LENGTH_SHORT).show();

            }
        });
    }
    private AdvertiseCallback advtCallBack = new AdvertiseCallback()
    {
        @Override
        public void onStartFailure(int errorCode)
        {
            switch (errorCode)
            {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    ShowMesg("An operation alredy in progress");
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    ShowMesg("Control data too large");
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    ShowMesg("Unsupported feature");
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    ShowMesg("Internal error, try Blutooth Restart Or Device Restart");
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    ShowMesg("Channel busy .Try later");
                    break;
            }
        };
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect)
        {

        }
    };

}
