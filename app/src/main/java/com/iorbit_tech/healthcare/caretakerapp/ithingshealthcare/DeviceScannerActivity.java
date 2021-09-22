package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

//implementing onclicklistener
public class DeviceScannerActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan, buttonRegister;
    private EditText textViewId, textViewName;
    String subemail, subdeviceId, subdeviceTag,subdeviceType, subdeviceName, output;
    Spinner tag, type;
    String[] spinnerType = {"Motion Sensor", "Pendant"};
    String[] spinnerTag = {"Bedroom", "Bathroom","Livingroom", "Kitchen"};

    //qr code scanner object
    private IntentIntegrator qrScan;

    DateFormat formatter = null;
    Date convertedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        subemail = bundle.getString("email");


        //View objects
        buttonScan = (Button) findViewById(R.id.btn_scan);
        buttonRegister = (Button) findViewById(R.id.btn_register);
        textViewId = (EditText) findViewById(R.id.device_id);
        //textViewName = (EditText) findViewById(R.id.device_name);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String url = sharedPreferences.getString(Config.SCAN_URL, "");

        System.out.println("hhhhhhhh"+url);
        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
       // buttonRegister.setOnClickListener(this);


        type =(Spinner)findViewById(R.id.device_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(DeviceScannerActivity.this, android.R.layout.simple_list_item_1, spinnerType);
        type.setPrompt("Select device type");
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                subdeviceType = type.getSelectedItem().toString();

                System.out.println("devicetype="+subdeviceType);

               // Toast.makeText(DeviceScannerActivity.this, spinnerDropDownView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        tag =(Spinner)findViewById(R.id.device_tag);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(DeviceScannerActivity.this, android.R.layout.simple_list_item_1, spinnerTag);
        tag.setPrompt("Select device tag");
        tag.setAdapter(adapter1);

        tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                subdeviceTag = tag.getSelectedItem().toString();

                System.out.println("devicetag="+subdeviceTag);

                // Toast.makeText(DeviceScannerActivity.this, spinnerDropDownView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });



        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendDeviceData();
            }
        });
    }



    void sendDeviceData()
    {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        String year = Integer.toString(currentYear);
        System.out.println("year"+year);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        String month = Integer.toString(currentMonth);
        System.out.println("month"+month);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        String day = Integer.toString(currentDay);
        System.out.println("Day"+day);
        //String today = year+"/"+month+"/"+day+"\'T\'"+"14:30:00"+"\'Z\'";
        String today = year+"-"+month+"-"+day+" "+"14:30";
        System.out.println("Date"+today);
        //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss'Z'");
       // String stringDateFormat = "14/09/2011";
        formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            convertedDate =(Date) formatter.parse(today);
            java.sql.Timestamp timeStampDate = new Timestamp(convertedDate.getTime());
            System.out.println("Today is " + timeStampDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
       // System.out.println("Date from dd/MM/yyyy String in Java : " + output);


        /*try {
            Date date = format.parse(today);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/




        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        output = textViewId.getText().toString();
        subdeviceId = output.replaceAll("..(?!$)", "$0:");
        System.out.println("scancolon" + output);
//subdeviceName =textViewName.getText().toString();
        subdeviceTag = tag.getSelectedItem().toString();
        System.out.println("devicetagggg="+subdeviceTag);
        subdeviceType = type.getSelectedItem().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = sharedPreferences.getString(Config.SCAN_URL, "");
        //final String url = "http://178.128.165.237/php/api/smart/registerAppDevice.php";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject sub=new JSONObject(response);
                            JSONArray array = sub.getJSONArray("responses");

                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject subscriber = array.getJSONObject(i);


    if(subscriber.has("error")) {
        String val = subscriber.getString("error");
        if(val.contains("A device with the given deviceId is already registered"))
        {
            displayError();

        }
    }

    if(subscriber.has("message")) {

        String val1 = subscriber.getString("message");
        System.out.println("scanresponses" + val1);
        if (val1.contains("Registered device for subscriber")) {
            displaySuccess();
        }
    }

                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                //String jsonData ="{\"eventKey:\"+alertid+,\"timeStamp\":alerttime,\"careTakerEmail\":email,\"actionType\":alertstatus,\"actionDesc\":note}";
                //String jsonData = String.format("{\"eventKey:\"%s,\"timeStamp\":%s,\"careTakerEmail\":%s,\"actionType\":%s,\"actionDesc\":%s}",alertid,alerttime,email,alertstatus,note) ;

                subemail = SubscriberActivity.submail;

                System.out.println("Now sub email"+subemail);

                String jsonData = "";
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("deviceId", subdeviceId); // Set the first name/pair

                    jsonObj.put("deviceType", subdeviceType);
                    jsonObj.put("deviceTag", subdeviceTag);
                    jsonObj.put("subscriber", subemail);

                    jsonData = jsonObj.toString();
                    System.out.println("datasend"+subdeviceTag);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return jsonData.getBytes();

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();


textViewId.setText("");
//textViewName.setText("");


    }

    void displayError()
    {
        Toast.makeText(this, "Already exist please register new device", Toast.LENGTH_LONG).show();
    }

    void displaySuccess()
    {


        textViewId.setText("");
        //textViewTag.setText("");
        Intent intent = new Intent(DeviceScannerActivity.this, SubscriberActivity.class);
        System.out.println("Scanning..............");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    String devid = result.getContents();
                    //setting values to textviews

                    //Toast.makeText(this, "ooooo"+devid, Toast.LENGTH_LONG).show();
                    textViewId.setText(devid);
                    //tv_qr_readTxt.setText(result.getContents());
                    //textViewAddress.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    String devid = result.getContents();
                    //setting values to textviews

                   // Toast.makeText(this, "ooooo"+devid, Toast.LENGTH_LONG).show();
                    textViewId.setText(devid);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


}