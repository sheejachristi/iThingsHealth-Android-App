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
import com.iorbit_tech.healthcare.caretakerapp.utils.CommonDataArea;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

//implementing onclicklistener
public class TemplateActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan, buttonRegister;
    private EditText textViewId, textViewName;
    //String subemail, subdeviceId, subdeviceTag,subeventType, sub, output;
    String subemail, careemail, templatename, eventname, eventtype,  starttime, endtime, timeout,  outsidesensor, timestampstart, timestampend;

    String[] sensorused = new String[3];
    String[] sequence = new String[5];

SubscriberActivity s1;
    long tstart, tend;
    Spinner tag, type, stime, etime,  osensor;
    String[] spinnerType = {"WakeUp", "Inactivity", "BathroomBreak"};
    String[] spinnerTag = {"Bedroom", "Bathroom","Livingroom", "Kitchen"};
    String[] spinnerOutside = {"Bedroom", "Livingroom", "Kitchen"};
    String[] spinnerSTime = {"0:00", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00", "5:30", "6:00", "6:30", "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"};
    String[] spinnerETime = {"0:00", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00", "5:30", "6:00", "6:30", "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"};
    String[] spinnerTimeslot = {"10 min", "30min", "60min"};

    String eventStartTime, eventEndTime;

    String name, description, eventType, category, appliesTo, actionname, tags;

    //qr code scanner object
    private IntentIntegrator qrScan;

    DateFormat formatter = null;
    Date convertedDate = null;

    DateFormat formatter1 = null;
    Date convertedDate1 = null;
    JSONArray mJSONArray = null;
    JSONObject finalobject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Bundle bundle = getIntent().getExtras();
        subemail = bundle.getString("email");


        SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        careemail = sharedPreferences1.getString(Config.EMAIL_SHARED_PREF, "bmjo@ebirdonline.com");
        System.out.println("caregiver is"+careemail);
        System.out.println("subscriber is"+bundle.getString("email"));

        //View objects

        buttonRegister = (Button) findViewById(R.id.btn_register);


        type =(Spinner)findViewById(R.id.event_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TemplateActivity.this, android.R.layout.simple_list_item_1, spinnerType);
        type.setPrompt("Select Event type");
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                eventtype = type.getSelectedItem().toString();

                System.out.println("Eventtype="+eventtype);

                if(eventtype == "WakeUp") {

                    tag.setVisibility(View.INVISIBLE);
                }

                else if(eventtype == "Inactivity") {

                    tag.setVisibility(View.INVISIBLE);
                }

                if(eventtype == "BathroomBreak") {

                    tag.setVisibility(View.VISIBLE);
                }

                // Toast.makeText(DeviceScannerActivity.this, spinnerDropDownView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        tag =(Spinner)findViewById(R.id.outside_sensor);
        tag.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(TemplateActivity.this, android.R.layout.simple_list_item_1, spinnerTag);
        tag.setPrompt("Select Outside Sensor");
        tag.setAdapter(adapter1);

        tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                outsidesensor = tag.getSelectedItem().toString();

                System.out.println("Sensor="+outsidesensor);

                // Toast.makeText(DeviceScannerActivity.this, spinnerDropDownView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        stime =(Spinner)findViewById(R.id.start_time);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(TemplateActivity.this, android.R.layout.simple_list_item_1, spinnerSTime);
        stime.setPrompt("Select start time");
        stime.setAdapter(adapter2);

        stime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                starttime = stime.getSelectedItem().toString();

                System.out.println("StartTime="+starttime);

                // Toast.makeText(DeviceScannerActivity.this, spinnerDropDownView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        etime =(Spinner)findViewById(R.id.end_time);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(TemplateActivity.this, android.R.layout.simple_list_item_1, spinnerETime);
        etime.setPrompt("Select end time");
        etime.setAdapter(adapter3);

        etime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                endtime = etime.getSelectedItem().toString();

                System.out.println("EndTime="+endtime);

                // Toast.makeText(DeviceScannerActivity.this, spinnerDropDownView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        osensor =(Spinner)findViewById(R.id.outside_sensor);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(TemplateActivity.this, android.R.layout.simple_list_item_1, spinnerOutside);
        osensor.setPrompt("Select outside sensor");
        osensor.setAdapter(adapter4);

        osensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                outsidesensor = osensor.getSelectedItem().toString();

                System.out.println("outside sensor="+outsidesensor);

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

                sendEventData();
            }
        });
    }



    @Override
    protected void onResume() {

        super.onResume();

        Bundle bundle = getIntent().getExtras();
        subemail = bundle.getString("email");


        SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        careemail = sharedPreferences1.getString(Config.EMAIL_SHARED_PREF, "bmjo@ebirdonline.com");
        System.out.println("caregiver is"+careemail);
        System.out.println("subscriber is"+bundle.getString("email"));




    }

    void sendEventData()
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
        timestampstart = year+"-"+month+"-"+day+" "+starttime;
        timestampend = year+"-"+month+"-"+day+" "+endtime;
        System.out.println("Date"+timestampstart);
        //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss'Z'");
        // String stringDateFormat = "14/09/2011";
        formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            convertedDate =(Date) formatter.parse(timestampstart);
            java.sql.Timestamp timeStampDate = new Timestamp(convertedDate.getTime());


            tstart = timeStampDate.getTime();
            eventStartTime = Long.toString(tstart);

            System.out.println("Today start is " + tstart);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        // System.out.println("Date from dd/MM/yyyy String in Java : " + output);


        formatter1 =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            convertedDate1 =(Date) formatter1.parse(timestampend);
            java.sql.Timestamp timeStampDate = new Timestamp(convertedDate1.getTime());


            tend = timeStampDate.getTime();
            eventEndTime = Long.toString(tend);

            System.out.println("Today end is " + tend);


        } catch (ParseException e) {
            e.printStackTrace();
        }


        /*try {
            Date date = format.parse(today);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/





        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://178.128.165.237/php/api/smart/createTemplateFromMobile.php";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        templatename = eventtype+date;

        List<Map<String, String>> values = new ArrayList<>();
        Map<String, String> maps = new HashMap<>();

        Map<String, String> maps1 = new HashMap<>();

        Map<String, String> maps2 = new HashMap<>();

        System.out.println("Templatename"+templatename);

        if(eventtype == "WakeUp")
        {
            timeout ="0";
            sequence[0] = "1";
            sensorused[0] = "Bedroom";

            tag.setVisibility(View.INVISIBLE);

            eventname = UUID.randomUUID().toString();
            System.out.println("Eventname"+eventname);
            maps.put("name", eventname);

            maps.put("startTime", eventStartTime);
            maps.put("endTime", eventEndTime);

            maps.put("timeOut", timeout);
            maps.put("generateEvent", "0");


            maps.put("sequence", "1");
            maps.put("tag", "Bedroom");



            values.add(maps);







            mJSONArray = new JSONArray(values);
            System.out.println("values in json array"+mJSONArray);


        }

        else if(eventtype == "Inactivity") {
            timeout = "30";
            sequence[0] = "1";
            sensorused[0] = "Bedroom";
            sequence[1] = "2";
            sensorused[1] = "Bathroom";
            sequence[2] = "3";
            sensorused[2] = "Kitchen";

            tag.setVisibility(View.INVISIBLE);

            eventname = UUID.randomUUID().toString();
            System.out.println("Eventname"+eventname);
            maps.put("name", eventname);

            maps.put("startTime", eventStartTime);
            maps.put("endTime", eventEndTime);

            maps.put("timeOut", timeout);
            maps.put("generateEvent", "NA");


            maps.put("sequence", "1");
            maps.put("tag", "Bedroom");

            values.add(maps);

            eventname = UUID.randomUUID().toString();
            System.out.println("Eventname"+eventname);
            maps1.put("name", eventname);

            maps1.put("startTime", eventStartTime);
            maps1.put("endTime", eventEndTime);

            maps1.put("timeOut", timeout);
            maps1.put("generateEvent", "NA");


            maps1.put("sequence", "2");
            maps1.put("tag", "Bathroom");

            values.add(maps1);


            eventname = UUID.randomUUID().toString();
            System.out.println("Eventname"+eventname);
            maps2.put("name", eventname);

            maps2.put("startTime", eventStartTime);
            maps2.put("endTime", eventEndTime);

            maps2.put("timeOut", timeout);
            maps2.put("generateEvent", "NA");


            maps2.put("sequence", "3");
            maps2.put("tag", "Kitchen");

            values.add(maps2);







            mJSONArray = new JSONArray(values);
            System.out.println("values in json array"+mJSONArray);



        }

        else if(eventtype == "BathroomBreak")
        {
            timeout ="30";
            sequence[0] = "1";
            sensorused[0] = "Bathroom";
            sequence[1] = "2";
            sensorused[1] = outsidesensor;

            tag.setVisibility(View.VISIBLE);

            eventname = UUID.randomUUID().toString();
            System.out.println("Eventname"+eventname);
            maps.put("name", eventname);

            maps.put("startTime", eventStartTime);
            maps.put("endTime", eventEndTime);

            maps.put("timeOut", timeout);
            maps.put("generateEvent", "NA");


            maps.put("sequence", "1");
            maps.put("tag", "Bathroom");

            values.add(maps);

            eventname = UUID.randomUUID().toString();
            System.out.println("Eventname"+eventname);
            maps1.put("name", eventname);

            maps1.put("startTime", eventStartTime);
            maps1.put("endTime", eventEndTime);

            maps1.put("timeOut", timeout);
            maps1.put("generateEvent", "NA");


            maps1.put("sequence", "2");
            maps1.put("tag", outsidesensor);

            values.add(maps1);










            mJSONArray = new JSONArray(values);
            System.out.println("values in json array"+mJSONArray);

        }










        /*JSONObject maps = null;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < sensorused.length; i++) {
            maps = new JSONObject();
            try {
                maps.put("name", eventname);

                maps.put("startTime", eventStartTime);
                maps.put("endTime", eventEndTime);

                maps.put("timeOut", timeout);
                maps.put("generateEvent", "0");

                maps.put("sequence", sequence[i]);
                maps.put("tag", sensorused[i]);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonArray.put(maps);
        }

        finalobject = new JSONObject();
        try {
            finalobject.put("eventDetails", jsonArray);
            System.out.println("values in json array"+finalobject);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        //JSONObject json = new JSONObject(values);



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
                                    if(val.contains("Already exist. Please create a new template"))
                                    {
                                        displayError();

                                    }
                                }

                                if(subscriber.has("message")) {

                                    String val1 = subscriber.getString("message");
                                    System.out.println("scanresponses" + val1);
                                    if (val1.contains("Template created successfully.")) {
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
                String jsonData = "";
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("name", templatename); // Set the first name/pair

                    jsonObj.put("description", "mobile event");
                    jsonObj.put("eventType", "normal");
                    jsonObj.put("category", eventtype);
                    jsonObj.put("appliesTo", "Subscriber");
                    jsonObj.put("tag", "tag");

                    jsonObj.put("eventDetails", mJSONArray );

                    jsonData = jsonObj.toString();
                    //System.out.println("datasend"+subdeviceTag);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return jsonData.getBytes();

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();


//        textViewId.setText("");
//textViewName.setText("");


    }

    void displayError()
    {
        Toast.makeText(this, "Already exist please register new event", Toast.LENGTH_LONG).show();
    }

    void displaySuccess()
    {






        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://178.128.165.237/php/api/smart/associateTemplateFromMobile.php";



        /*final List<Map<String, String>> values = new ArrayList<Map<String, String>>();
        Map<String, String> maps = new HashMap<>();
        maps.put("name", "BqYWKWzs4r8SyGQvyqH0");

        maps.put("startTime", "16:30");
        maps.put("endTime", "18:30");

        maps.put("timeOut", "10");
        maps.put("generateEvent", "0");

        maps.put("sequence", "1");
        maps.put("tag", "bedroom sensor");
        values.add(maps);
        final JSONArray mJSONArray = new JSONArray(values);*/

        //JSONObject json = new JSONObject(values);



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
                                    if(val.contains("Can't insert data"))
                                    {
                                        displayError();

                                    }
                                }

                                if(subscriber.has("message")) {

                                    String val1 = subscriber.getString("message");
                                    System.out.println("scanresponses" + val1);
                                    if (val1.contains("Successfully associated template to subscriber.")) {
                                        System.out.println("Successfully associate template to subscriber");
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
                    jsonObj.put("eventName", templatename); // Set the first name/pair

                    jsonObj.put("Subscriber", subemail);
                    jsonObj.put("actionName", "action");
                    jsonObj.put("useDefaults", "xxx");
                    jsonObj.put("priority", "1");
                    jsonObj.put("recipients", careemail);

                    jsonObj.put("message", "Template from mobile");

                    jsonObj.put("deliveryType", "sms");

                    jsonData = jsonObj.toString();
                    //System.out.println("datasend"+subdeviceTag);




                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return jsonData.getBytes();

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();






        //textViewTag.setText("");
        Intent intent = new Intent(TemplateActivity.this, SubscriberActivity.class);
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