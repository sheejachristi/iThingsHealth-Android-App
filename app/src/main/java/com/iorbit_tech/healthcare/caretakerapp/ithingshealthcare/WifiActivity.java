package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import com.iorbit_tech.healthcare.caretakerapp.utils.LogWriter;

import android.Manifest;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

    public class WifiActivity extends ListActivity {
        WifiManager mainWifiObj;
        WifiScanReceiver wifiReciever;
        ListView list;
        String wifis[];

        EditText pass;


        BleScanner bleScan;
        String btAddress ;
        BleAdvertiserEx advtr;// = new BleAdvertiserEx();
        BleScanner mokoScan;
        String checkPassword;


        String wifiname, wifipass;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            turnGPSOn();

            setContentView(R.layout.activity_wifi_main);

            list=getListView();
            mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiReciever = new WifiScanReceiver();
            mainWifiObj.startScan();

            showRequestPermissionDialog2();

            mokoScan = new BleScanner();
            mokoScan.InitMokoScanner(this);
            mokoScan.StartMokoScan();
            bleScan= new BleScanner();
            bleScan.InitScanner(this);


            // listening to single list item on click
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // selected item
                    String ssid = ((TextView) view).getText().toString();
                    connectToWifi(ssid);
                    Toast.makeText(WifiActivity.this,"Wifi SSID : "+ssid,Toast.LENGTH_SHORT).show();

                }
            });

            advtr = new BleAdvertiserEx();
            advtr.InitAdvertiser(this);


        }


        public void turnGPSOn(){
            try
            {

                String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);


                if(!provider.contains("gps")){ //if gps is disabled
                    final Intent poke = new Intent();
                    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                    poke.setData(Uri.parse("3"));
                    sendBroadcast(poke);
                }
            }
            catch (Exception e) {

            }
        }


        private void showRequestPermissionDialog2() {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        }

        protected void onPause() {
            unregisterReceiver(wifiReciever);
            super.onPause();
        }

        protected void onResume() {
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            super.onResume();
        }
        class WifiScanReceiver extends BroadcastReceiver {
            @SuppressLint("UseValueOf")
            public void onReceive(Context c, Intent intent) {
                List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
                wifis = new String[wifiScanList.size()];
                for(int i = 0; i < wifiScanList.size(); i++){
                    wifis[i] = ((wifiScanList.get(i)).toString());
                }
                String filtered[] = new String[wifiScanList.size()];
                int counter = 0;
                for (String eachWifi : wifis) {
                    String[] temp = eachWifi.split(",");

                    filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                    counter++;

                }
                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,R.id.label, filtered));


            }
        }

        private void finallyConnect(String networkPass, String networkSSID) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", networkSSID);
            wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

            // remember id
            int netId = mainWifiObj.addNetwork(wifiConfig);
            mainWifiObj.disconnect();
            mainWifiObj.enableNetwork(netId, true);
            mainWifiObj.reconnect();

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"\"" + networkSSID + "\"\"";
            conf.preSharedKey = "\"" + networkPass + "\"";
            mainWifiObj.addNetwork(conf);
        }

        private void connectToWifi(final String wifiSSID) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.connect);
            dialog.setTitle("Send to Network");
            TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

            Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
            pass = (EditText) dialog.findViewById(R.id.textPassword);
            textSSID.setText(wifiSSID);

            // if button is clicked, connect to the network;
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPassword = pass.getText().toString();
                    pass.setText("");
                    dialog.dismiss();
                    //finallyConnect(checkPassword, wifiSSID);
                    /*Intent intent = new Intent(WifiActivity.this, WifiDetails.class);
                    System.out.println("Wifi_OPENED.............."+checkPassword);
                    intent.putExtra("wifiname",wifiSSID);
                    intent.putExtra("wifipass",checkPassword);
                    dialog.dismiss();
                    startActivity(intent);*/

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BTAdvtHotSpot(wifiSSID, checkPassword);
                        }
                    }).start();

                }
            });
            dialog.show();
        }


        void BTAdvtHotSpot( String wifiHotspot, String wifiPasswd)
        {
            try {
                System.out.println("wifiname"+wifiHotspot+"wifipass"+wifiPasswd);
                advtr.SetWIFIHotSpot(wifiHotspot);
               //advtr.StartAdvertising(10);
                LogWriter.writeLog("wifi name", "wifi details");
               Thread.sleep(10000);
                advtr.SetWIFIPassword(wifiPasswd);
                //advtr.StartAdvertising(10);
                Intent intent = new Intent(WifiActivity.this, SubscriberActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }catch(Exception exp){

            }

        }
    }