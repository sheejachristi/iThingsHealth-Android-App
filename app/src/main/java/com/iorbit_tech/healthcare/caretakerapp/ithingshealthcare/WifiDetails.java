package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iorbit_tech.healthcare.caretakerapp.ble.BleAdvertiserEx;
import com.iorbit_tech.healthcare.caretakerapp.ble.BleScanner;
import com.iorbit_tech.healthcare.caretakerapp.ble.SensorEventNotify;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiDetails extends AppCompatActivity {

    BleScanner bleScan;
    String btAddress ;
    BleAdvertiserEx advtr;// = new BleAdvertiserEx();
    private EditText editTextwifiname;
    private EditText editTextwifipass;
    private AppCompatButton buttonSend;


    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    String wifiname, wifipass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        editTextwifiname = (EditText) findViewById(R.id.input_wifiname);
        editTextwifipass = (EditText) findViewById(R.id.input_wifipass);
        Bundle bundle = getIntent().getExtras();
        wifiname = bundle.getString("wifiname");
        wifipass = bundle.getString("wifipass");

        //String wifiname = editTextwifiname.getText().toString();
        //String wifipass = editTextwifipass.getText().toString();
        editTextwifiname.setText(wifiname);
        editTextwifipass.setText(wifipass);

        buttonSend = (AppCompatButton) findViewById(R.id.btn_send);




        advtr = new BleAdvertiserEx();
        advtr.InitAdvertiser(this);


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BTAdvtHotSpot(wifiname, wifipass);
            }
        });

        /*Button buttonB = (Button)findViewById(R.id.button2);
        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advtr.StopAdvertising();
            }
        });*/


    }

    byte seqNum=0;
    void BtAdvertise( String bookingID)
    {

        advtr.SetTocken("bk:"+bookingID);
        advtr.StartAdvertising(10);
    }

    void BTAdvtHotSpot( String wifiHotspot, String wifiPasswd)
    {
        try {
            System.out.println("wifiname"+wifiHotspot+"wifipass"+wifiPasswd);
            advtr.SetWIFIHotSpot(wifiHotspot);
            advtr.StartAdvertising(10);
            Thread.sleep(10);
            advtr.SetWIFIPassword(wifiPasswd);
            advtr.StartAdvertising(10);
            Intent intent = new Intent(WifiDetails.this, SubscriberActivity.class);
            startActivity(intent);
        }catch(Exception exp){

        }

    }




}
