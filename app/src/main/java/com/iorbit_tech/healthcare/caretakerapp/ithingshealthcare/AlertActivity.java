package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.iorbit_tech.healthcare.caretakerapp.utils.CommonDataArea;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class AlertActivity extends AppCompatActivity implements View.OnClickListener {


   EditText alertnote;
   Button submit;
String value, alertId, dialog_close;
boolean flag;
Spinner spinner;
    //Spinner fromList;
    Spinner toList;
    ArrayAdapter<CharSequence> adapter;
    private Dao<AlertDetail, Integer> alertDao;
    public Database_Helper database_helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertstatus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //value = "";
        //alertId = "";


        alertnote = (EditText) findViewById(R.id.input_note);
        submit = (Button) findViewById(R.id.btn_alertnote);
        spinner = (Spinner) findViewById(R.id.spinner1);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.alert_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //spinner.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        dialog_close = sharedPreferences.getString(Config.DIALOG_CLOSE, "");
        int indexEvent = sharedPreferences.getInt(Config.CURRENT_INDEX,1);
        final String dialog_status = sharedPreferences.getString(Config.DIALOG_CLOSE+"_"+indexEvent, "");
        if(dialog_status.equals("open"))
        {
            spinner.setVisibility(View.INVISIBLE);
            flag = false;

        }
        else
        {
            spinner.setVisibility(View.VISIBLE);
            flag = true;
        }



            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    closeStatus();

                }
            });




        }

    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        dialog_close = sharedPreferences.getString(Config.DIALOG_CLOSE, "");
        int indexEvent = sharedPreferences.getInt(Config.CURRENT_INDEX,1);
        final String dialog_status = sharedPreferences.getString(Config.DIALOG_CLOSE+"_"+indexEvent, "");
        if(dialog_status.equals("open"))
        {
            spinner.setVisibility(View.INVISIBLE);
            flag = false;

        }
        else
        {
            spinner.setVisibility(View.VISIBLE);
            flag = true;
        }



    }

    @Override
    public void onClick(View v) {
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }




    public void closeStatus() {

        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("alertkey");
            alertId = extras.getString("alertUUId");
            //The key argument here must match that used in the other activity
        }*/
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        value = sharedPreferences.getString(Config.ALERTKEY, "");

        System.out.println("Check key"+value);
        alertId = sharedPreferences.getString(Config.ALERTID, "");

        int indexEvent = sharedPreferences.getInt(Config.CURRENT_INDEX, 1);


        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //Adding values to editor

        //editor.putString(Config.DIALOG_CLOSE, "close");
        //editor.putBoolean(Config.MESGRECVD_SHARED_PREF+"_"+indexEvent, true);
       /* editor.putString(Config.MESGTITLE_SHARED_PREF+"_"+indexEvent, "");
        editor.putString(Config.MESGBODY_SHARED_PREF+"_"+indexEvent, "");
        editor.putLong(Config.MESGRECVD_TIME_SHARED_PREF+"_"+indexEvent, 0);
        //editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF, indexEvent);
        editor.putString(Config.ALERT_KEY+"_"+indexEvent,"");

        editor.putString(Config.MESGRECVD_EMAIL+"_"+indexEvent,"");
        editor.putString(Config.MESGRECVD_EVENTNAME+"_"+indexEvent,"");
        editor.putString(Config.MESGRECVD_PHONE+"_"+indexEvent,"");
        //editor.putString(Config.MESGRECVD_IMG_URL+"_"+indexEvent,values.get("imageURL"));
        editor.putString(Config.MESGRECVD_IMG_URL+"_"+indexEvent,"");
        editor.putString(Config.MESGRECVD_SUBSCRIBER+"_"+indexEvent,"");
        editor.putString(Config.ALERT_KEY+"_"+indexEvent,"");
        editor.putString(Config.ALERT_UUID+"_"+indexEvent,"");*/


//String indexEventval = Integer.toString(indexEvent);


            /*editor.remove(Config.MESGTITLE_SHARED_PREF+"_"+indexEvent);

        editor.remove(Config.MESGBODY_SHARED_PREF + "_" + indexEvent);
        //eventDesc = title + "->" + body;

        editor.remove(Config.MESGRECVD_IMG_URL + "_" + indexEvent);
        editor.remove(Config.MESGRECVD_SUBSCRIBER + "_" + indexEvent);

        editor.remove(Config.ALERT_KEY + "_" + indexEvent);
        editor.remove(Config.ALERT_UUID + "_" + indexEvent);
        editor.remove(Config.MESGRECVD_PHONE + "_" + indexEvent);
        editor.remove(Config.MESGRECVD_TIME_SHARED_PREF + "_" + indexEvent);
        editor.remove(Config.MESGRECVD_EMAIL + "_" + indexEvent);
        editor.remove(Config.MESGRECVD_EVENTNAME + "_" + indexEvent);*/
        //--indexEvent;


        System.out.println("xxxx" + alertId);

        //final String alertstatus = "Closed";


        //final String alertid = "100";
        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());


        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "bmjo@ebirdonline.com");
        final String alertid = sharedPreferences.getString(Config.ALERT_KEY, "");
        final String url = sharedPreferences.getString(Config.ALERT_URL, "");


        if (flag == true) {


            final String alertstatus = spinner.getSelectedItem().toString();

            System.out.println("alertUUId=" + alertId);
            System.out.println("status yyyyy" + alertstatus);


            if (alertstatus.equals("Attending")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.DIALOG_CLOSE + "_" + indexEvent, "open");

                editor.commit();
                try
                {
                    alertDao =getHelper().getInformationDao();
                UpdateBuilder<AlertDetail, Integer> updateBuilder = alertDao.updateBuilder();

// set the criteria like you would a QueryBuilder
                updateBuilder.where().eq("alert_uuid", alertId);
// update the value of your field(s)
                    updateBuilder.updateColumnValue("alert_status", "open");
                updateBuilder.update();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }

                System.out.println("IndexEvent" + indexEvent + "Dialog Close" + sharedPreferences.getString(Config.DIALOG_CLOSE + "_" + indexEvent, ""));
                final String note;
                String noteval = alertnote.getText().toString().trim();

                if (noteval.equals("")) {

                    note = "attending";
                } else {
                    note = noteval;
                }
                final String status = "3";
                final long alerttime = System.currentTimeMillis();
                System.out.println("Time" + alerttime);
                System.out.println("eventKey"+ value);
                System.out.println("careTakerEmail"+ email);
                System.out.println("actionType"+ alertstatus);
                System.out.println("actionDesc"+ note);
                System.out.println("status"+ status);
                System.out.println("alertUUId"+ alertId);



                //Creating a string request
                RequestQueue queue = Volley.newRequestQueue(this);


                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // your response

                                System.out.println("getting"+response);

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
                            jsonObj.put("eventKey", value); // Set the first name/pair
                            jsonObj.put("timeStamp", alerttime);
                            jsonObj.put("careTakerEmail", email);
                            jsonObj.put("actionType", alertstatus);
                            jsonObj.put("actionDesc", note);
                            jsonObj.put("status", status);
                            jsonObj.put("alertUUId", alertId);
                            jsonData = jsonObj.toString();
                            System.out.println("insertUUID=" + alertId);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        return jsonData.getBytes();

                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                queue.start();
                alertnote.setText("");
                //spinner.setVisibility(View.INVISIBLE);
            /*String pos = spinner.getSelectedItem().toString();
            int position = adapter.getPosition(pos);
            if(position >= 0){
                adapter.remove(pos);
                adapter.notifyDataSetChanged();
            }*/
                Intent intent = new Intent(AlertActivity.this, SubscriberActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
            else if (alertstatus.equals("Close")) {
                final String note = alertnote.getText().toString().trim();
                //final String alertstatus = "close";
                if (note.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter note", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

                    final long alerttime = System.currentTimeMillis();
                    System.out.println("Time" + alerttime);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.DIALOG_CLOSE + "_" + indexEvent, "close");
                    System.out.println("index--------------" + indexEvent);
                    editor.commit();

                    try
                    {
                        alertDao =getHelper().getInformationDao();
                        UpdateBuilder<AlertDetail, Integer> updateBuilder = alertDao.updateBuilder();

// set the criteria like you would a QueryBuilder
                        //updateBuilder.where().eq("alert_uuid", alertId);
// update the value of your field(s)
                        updateBuilder.updateColumnValue("alert_status", "close").where().eq("alert_uuid", alertId);
                        updateBuilder.update();
                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                    }

                    System.out.println("index--------------" + indexEvent + "Dialog Close" + Config.DIALOG_CLOSE + "_" + indexEvent);
                    final String status = "5";
                    //Creating a string request
                    RequestQueue queue = Volley.newRequestQueue(this);


                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // your response
                                    System.out.println("getting"+response);
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
                                jsonObj.put("eventKey", value); // Set the first name/pair
                                jsonObj.put("timeStamp", alerttime);
                                jsonObj.put("careTakerEmail", email);
                                jsonObj.put("actionType", alertstatus);
                                jsonObj.put("actionDesc", note);
                                jsonObj.put("status", status);

                                jsonObj.put("alertUUId", alertId);
                                jsonData = jsonObj.toString();
                                System.out.println("insertUUID=" + alertId);

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            return jsonData.getBytes();

                        }
                    };
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    queue.start();
                    alertnote.setText("");
                    Intent intent = new Intent(AlertActivity.this, SubscriberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }
            }


            } else {

                final String note = alertnote.getText().toString().trim();
                final String alertstatus = "close";
                if (note.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter note", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

                    final long alerttime = System.currentTimeMillis();
                    System.out.println("Time" + alerttime);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.DIALOG_CLOSE + "_" + indexEvent, "close");
                    System.out.println("index--------------" + indexEvent);
                    editor.commit();
                    System.out.println("index--------------" + indexEvent + "Dialog Close" + Config.DIALOG_CLOSE + "_" + indexEvent);
                    final String status = "5";
                    //Creating a string request
                    RequestQueue queue = Volley.newRequestQueue(this);


                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // your response

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
                                jsonObj.put("eventKey", value); // Set the first name/pair
                                jsonObj.put("timeStamp", alerttime);
                                jsonObj.put("careTakerEmail", email);
                                jsonObj.put("actionType", alertstatus);
                                jsonObj.put("actionDesc", note);
                                jsonObj.put("status", status);

                                jsonObj.put("alertUUId", alertId);
                                jsonData = jsonObj.toString();
                                System.out.println("insertUUID=" + alertId);

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            return jsonData.getBytes();

                        }
                    };
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    queue.start();
                    alertnote.setText("");
                    Intent intent = new Intent(AlertActivity.this, SubscriberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }
            }


        }
    private Database_Helper getHelper() {
        if (database_helper == null) {
            database_helper = OpenHelperManager.getHelper(this, Database_Helper.class);
        }
        return database_helper;
    }



}