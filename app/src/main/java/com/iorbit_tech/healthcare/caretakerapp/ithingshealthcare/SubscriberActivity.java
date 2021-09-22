package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iorbit_tech.healthcare.caretakerapp.utils.CommonDataArea;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SubscriberActivity extends AppCompatActivity {


    //a list to store all the products

    private Context mCtx;

    //the recyclerview
    RecyclerView recyclerView;
    EditText eventRecvd;
    Button btn_alerts;
    public  String alertkeyval;
    Dialog dialog;

    private boolean settingsIn = false;

    Timer timer;
    public Database_Helper database_helper = null;
    private List<AlertDetail> alertList, alertList1;
    private Dao<AlertDetail, Integer> alertDao;
    int index = 0;
    String sub_email;
    List list;
    static String submail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
        CommonDataArea.view_created(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber);
        dialog = new Dialog(this);

        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.subscriber);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //initializing the productlist
        CommonDataArea.subscribersList = new ArrayList<>();


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        CommonDataArea.popup = sharedPreferences.getBoolean(Config.DIASBLE_POP_SHARED_PREF,true);
        CommonDataArea.beep = sharedPreferences.getBoolean(Config.DISABLE_BEEP_SHARED_PREF,true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if((dialog!=null)&&(dialog.isShowing())){
                    if(CommonDataArea.beep){
                        try {
                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
                            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 1000);

                            Thread.sleep(1500);
                            toneG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
                            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 1000);
                        }catch(Exception exp){

                        }
                    }

                }
            }
        },15000,5000);

    }


    @Override
    protected void onResume() {

        super.onResume();

        CommonDataArea.view_resumed(this);
        loadSubscribers();

        checkLastEventReported(null,false);
        //updateStatus();

    }

    @Override
    protected void onStop() {
        CommonDataArea.view_stopped(this);
        super.onStop();

    }

    @Override
    protected void onPause() {
        CommonDataArea.view_paused(this);
        super.onPause();
        dialog.dismiss();

    }
    @Override
    protected void onDestroy() {
        CommonDataArea.view_destroyed(this);
        super.onDestroy();
        dialog.dismiss();

    }

    String eventDesc;
    long eventTime;
    String subsName;
    String imgurl;
    String phoneNum;
    String eventName;
    String email;
    int lastEventIndex;
    int curIndex;
    String alertUUId;
    Boolean check;
    TextView textEventDesc;
    TextView textnameSubs;
    TextView textTime;
    TextView textEventName;
    TextView textPhone;
    TextView textTitle;
    ImageView subscriber;
    TextView textIndex;

    void readEventAtIndex(int index, SharedPreferences sharedPreferences) {
        String title = sharedPreferences.getString(Config.MESGTITLE_SHARED_PREF + "_" + index, "");
        String body = sharedPreferences.getString(Config.MESGBODY_SHARED_PREF + "_" + index, "");
        //eventDesc = title + "->" + body;
        eventDesc = body;
        imgurl = sharedPreferences.getString(Config.MESGRECVD_IMG_URL + "_" + index, "");
        subsName = sharedPreferences.getString(Config.MESGRECVD_SUBSCRIBER + "_" + index, "");
        System.out.println("Alert Key..."+sharedPreferences.getString(Config.ALERT_KEY + "_" + index, ""));
        alertkeyval = sharedPreferences.getString(Config.ALERT_KEY + "_" + index, "");
        alertUUId = sharedPreferences.getString(Config.ALERT_UUID + "_" + index, "");

        System.out.println("here uuid"+alertUUId);
        phoneNum = sharedPreferences.getString(Config.MESGRECVD_PHONE + "_" + index, "");
        eventTime = sharedPreferences.getLong(Config.MESGRECVD_TIME_SHARED_PREF + "_" + index, 0);
        email = sharedPreferences.getString(Config.MESGRECVD_EMAIL + "_" + index, "");
        eventName = sharedPreferences.getString(Config.MESGRECVD_EVENTNAME + "_" + index, "");
        int indexColun = eventName.indexOf(':');
        if (eventName.length() > indexColun)
            eventName = eventName.substring(indexColun + 1);
    }






    int findNextEvent(int index, String subsEmail,SharedPreferences sharedPreferences){
        while (true) {
        String subsName1 = sharedPreferences.getString(Config.MESGRECVD_EMAIL + "_" + index, "");
        if (subsName1.contains(subsEmail)) {
           return index;

        }
            if (index > 1) index--;
        else return 0;
        }
    }

    boolean readNextEventAfterIndex(int index, String subsEmail) {
        final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        lastEventIndex = sharedPreferences.getInt(Config.MESGRECVD_INDEX_SHARED_PREF, 0);


        boolean found = false;
        if (found) {
            String title = sharedPreferences.getString(Config.MESGTITLE_SHARED_PREF + "_" + index, "");
            String body = sharedPreferences.getString(Config.MESGBODY_SHARED_PREF + "_" + index, "");
            eventDesc = title + "->" + body;
            imgurl = sharedPreferences.getString(Config.MESGRECVD_IMG_URL + "_" + index, "");

            subsName = sharedPreferences.getString(Config.MESGRECVD_SUBSCRIBER + "_" + index, "");
            phoneNum = sharedPreferences.getString(Config.MESGRECVD_PHONE + "_" + index, "");
            //eventTime = sharedPreferences.getLong(Config.MESGRECVD_TIME_SHARED_PREF + "_" + index, 0);
            email = sharedPreferences.getString(Config.MESGRECVD_EMAIL + "_" + index, "");
            eventName = sharedPreferences.getString(Config.MESGRECVD_EVENTNAME + "_" + index, "");
            int indexColun = eventName.indexOf(':');
            if (eventName.length() > indexColun)
                eventName = eventName.substring(indexColun + 1);
            curIndex = index;
        }
        return found;
    }

    void checkLastEventReported(final String subsEmail, boolean fromMenu) {
        /*SharedPreferences sharedPreferences2 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String dialogclose = sharedPreferences2.getString(Config.ALERTID, "");
        if (dialogclose.equals(alertUUId)) {
            int indexEvent = sharedPreferences2.getInt(Config.MESGRECVD_INDEX_SHARED_PREF,1);
            --indexEvent;
            SharedPreferences.Editor editor = sharedPreferences2.edit();
            editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF, indexEvent);
            editor.commit();
        }*/

        if((!fromMenu)&&(!CommonDataArea.popup)) return;;
        final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        lastEventIndex = sharedPreferences.getInt(Config.MESGRECVD_INDEX_SHARED_PREF, 1);
        curIndex = lastEventIndex;

        long lastEventTime = sharedPreferences.getLong(Config.MESGRECVD_TIME_SHARED_PREF + "_" + lastEventIndex, 0);
       // if ((System.currentTimeMillis() - lastEventTime) < Config.CONST_ONEHOUR_MILLS) {
        if(subsEmail!=null){
            curIndex=  findNextEvent(lastEventIndex,subsEmail,sharedPreferences);
            System.out.println("TEST___________"+curIndex);
        }
        if(curIndex>1) {
            readEventAtIndex(curIndex, sharedPreferences);
            System.out.println("Curindex--------------"+curIndex);


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Config.CURRENT_INDEX, curIndex);
            editor.commit();

            //final String alerttime = timestamp.toString();
            SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String email = sharedPreferences1.getString(Config.EMAIL_SHARED_PREF, "bmjo@ebirdonline.com");
            final String dialog_status = sharedPreferences1.getString(Config.DIALOG_CLOSE+"_"+curIndex, "");

            System.out.println("Dialog status="+dialog_status);

            //final String eventid = "acc501d0-9ff1-4338-8c0a-1a10df8b5d9e";


            if((dialog!=null)&&(dialog.isShowing())) dialog.dismiss();

                //final Dialog dialog = new Dialog(this);
                dialog = new Dialog(this);


                dialog.setContentView(R.layout.activity_alert);
                dialog.setTitle("Alert...");
            final Button attend = (Button) dialog.findViewById(R.id.btnattend);
            Button viewalert = (Button) dialog.findViewById(R.id.btnviewalert);
                // set the custom dialog components - text, image and button
                textIndex = (TextView) dialog.findViewById(R.id.eventindex);
                textTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
                textEventDesc = (TextView) dialog.findViewById(R.id.eventRecvd);
                textnameSubs = (TextView) dialog.findViewById(R.id.eventSubName);
                textTime = (TextView) dialog.findViewById(R.id.eventTime);
                textEventName = (TextView) dialog.findViewById(R.id.eventTime);
                textPhone = (TextView) dialog.findViewById(R.id.eventPhone);
                subscriber = (ImageView) dialog.findViewById(R.id.subscriber);
                new DownLoadImageTask(subscriber).execute(imgurl);


                textIndex.setText("" + curIndex);
                textTitle.setText(eventName);
                //textEventDesc.setText(eventDesc + "---" + alertUUId);
                textEventDesc.setText(eventDesc);
                textnameSubs.setText(subsName);
                //textTime.setText(Long.toString(eventTime));
                if (eventTime > 0) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm aa");
                    Date dateTimeEvent = new Date();
                    dateTimeEvent.setTime(eventTime);
                    String timeStr = dateFormat.format(dateTimeEvent);
                    textTime.setText(timeStr);
                } else {
                    textTime.setText("Time not available");
                }
                //textEventName.setText(eventName);
                textPhone.setText(phoneNum);
                subscriber.setImageResource(getResources().getIdentifier(imgurl, "drawable", "com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare"));


                ImageButton dialogButtonClose = (ImageButton) dialog.findViewById(R.id.btnClose);
                // if button is clicked, close the custom dialog
                dialogButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            dialog.show();
            if(dialog_status.equals("open")) {
                attend.setBackgroundResource(R.drawable.buttonshape);
                attend.setEnabled(true);
                attend.setText("Close");
            }


            else if(dialog_status.equals("close")) {
                attend.setEnabled(false);
                attend.setBackgroundColor(Color.parseColor("#696969"));
                attend.setText("Attended");
            }
            else
            {
                attend.setEnabled(true);
                attend.setBackgroundResource(R.drawable.buttonshape);
                attend.setText("Attend");
            }


                ImageButton dialogButtonNext = (ImageButton) dialog.findViewById(R.id.btnNext);
                ImageButton dialogButtonPrev = (ImageButton) dialog.findViewById(R.id.btnPrev);
                // if button is clicked, close the custom dialog
                dialogButtonPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (curIndex < lastEventIndex) {
                            ++curIndex;
                            //SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(Config.CURRENT_INDEX, curIndex);
                            editor.commit();
                            System.out.println("CurindexNext--------------"+curIndex);

                            if (subsEmail != null) {
                                curIndex = findNextEvent(curIndex, subsEmail, sharedPreferences);
                            }
                            readEventAtIndex(curIndex, sharedPreferences);

                            textIndex.setText("" + curIndex);
                            textTitle.setText(eventName);
                            textEventDesc.setText(eventDesc);
                            textnameSubs.setText(subsName);
                            if (eventTime > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm aa");
                                Date dateTimeEvent = new Date();
                                dateTimeEvent.setTime(eventTime);
                                String timeStr = dateFormat.format(dateTimeEvent);
                                textTime.setText(timeStr);
                            } else {
                                textTime.setText("Time not available");
                            }
                            //textTime.setText(Long.toString(eventTime));

                            //textEventName.setText(eventName);
                            textPhone.setText(phoneNum);
                            //subscriber.setImageResource(getResources().getIdentifier(imgurl, "drawable", "com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare"));
                            System.out.println("Image:" + imgurl);
                            subscriber.setImageResource(getResources().getIdentifier(imgurl, "drawable", "com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare"));

                            if ((imgurl != null) && (imgurl != "")) {
                                new DownLoadImageTask(subscriber).execute(imgurl);
                            } else {
                                ImageView image = (ImageView) dialog.findViewById(R.id.subscriber);
                                image.setImageResource(R.drawable.man);
                            }
                            //final String alerttime = timestamp.toString();
                            SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            final String dialog_status = sharedPreferences1.getString(Config.DIALOG_CLOSE+"_"+curIndex, "");

                            System.out.println("Dialog statusNext="+dialog_status);

                            if(dialog_status.equals("open")) {
                                attend.setBackgroundResource(R.drawable.buttonshape);
                                attend.setEnabled(true);
                                attend.setText("Close");
                            }

                            else if(dialog_status.equals("close")) {
                                attend.setEnabled(false);
                                attend.setBackgroundColor(Color.parseColor("#696969"));
                                attend.setText("Attended");
                            }
                            else
                            {
                                attend.setEnabled(true);
                                attend.setBackgroundResource(R.drawable.buttonshape);
                                attend.setText("Attend");
                            }
                        }
                    }
                });


                // if button is clicked, close the custom dialog
                dialogButtonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (curIndex > 1) {
                            --curIndex;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(Config.CURRENT_INDEX, curIndex);
                            editor.commit();
                            System.out.println("CurindexPrev--------------"+curIndex);
                            if (subsEmail != null) {
                                curIndex = findNextEvent(curIndex, subsEmail, sharedPreferences);
                            }
                            readEventAtIndex(curIndex, sharedPreferences);

                            textIndex.setText("" + curIndex);
                            textTitle.setText(eventName);
                            textEventDesc.setText(eventDesc);
                            textnameSubs.setText(subsName);
                            if (eventTime > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm aa");
                                Date dateTimeEvent = new Date();
                                dateTimeEvent.setTime(eventTime);
                                String timeStr = dateFormat.format(dateTimeEvent);
                                textTime.setText(timeStr);
                            } else {
                                textTime.setText("Time not available");
                            }
                            //textEventName.setText(eventName);
                            textPhone.setText(phoneNum);
                            if ((imgurl != null) && (imgurl != "")) {
                                new DownLoadImageTask(subscriber).execute(imgurl);
                            } else {
                                ImageView image = (ImageView) dialog.findViewById(R.id.subscriber);
                                image.setImageResource(R.drawable.man);
                            }
                            SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            final String dialog_status = sharedPreferences1.getString(Config.DIALOG_CLOSE+"_"+curIndex, "");

                            System.out.println("Dialog statusPrev="+dialog_status);

                            if(dialog_status.equals("open")) {
                                attend.setEnabled(true);
                                attend.setBackgroundResource(R.drawable.buttonshape);

                                attend.setText("Close");
                            }
                            else if(dialog_status.equals("close")) {
                                attend.setEnabled(false);
                                attend.setBackgroundColor(Color.parseColor("#696969"));
                                attend.setText("Attended");
                            }
                            else
                            {
                                attend.setEnabled(true);
                                attend.setBackgroundResource(R.drawable.buttonshape);
                                attend.setText("Attend");
                            }
                        }
                    }
                });



                // if button is clicked, close the custom dialog
                attend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                        updateStatus();

                    }
                });

            viewalert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    alertDialogBuilder.setMessage("No Events reported in last 1 Hour");
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();*/

                    Toast toast=Toast.makeText(getApplicationContext(),"Yet to construct",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });
           // }


                // dialog.show();
            }else{
            if(fromMenu) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("No Events reported in last 1 Hour");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            }


    }
    void eventDetails(String email)
    {
        sub_email = email;
        index = 0;
        //list= null;
        try
        {

            alertDao =getHelper().getInformationDao();
            QueryBuilder<AlertDetail,Integer> queryBuilder = alertDao.queryBuilder();

            queryBuilder.where().like("alert_status", "open").or().like("alert_status", "attend").and().like("alert_email", email);
            list = queryBuilder.query();
            if(list.size()==0)
            {
                System.out.println("EMPTY------------------");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("No Events reported for this subscriber");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }



            else if(index<list.size()){
                final AlertDetail alert_detail = (AlertDetail) list.get(index);

                System.out.println("LIsting------------------" + alert_detail.alert_name);

                openDialog(alert_detail.alert_name, alert_detail.alert_msg, alert_detail.alert_time, alert_detail.alert_subscriber, alert_detail.alert_status, alert_detail.alert_index, alert_detail.alert_phone, alert_detail.sub_image);

            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    void deviceScan(String email) {

        Intent intent = new Intent(SubscriberActivity.this, DeviceScannerActivity.class);
        System.out.println("Scanning..............");
        intent.putExtra("email",email);
        startActivity(intent);
    }


    void generateEvent(String email) {
        sub_email = "";
        sub_email = email;

submail = email;
        Intent intent = new Intent(SubscriberActivity.this, TemplateActivity.class);
        System.out.println("sub email in template:"+sub_email);
        intent.putExtra("email",sub_email);
        startActivity(intent);
    }




    void openDialog(String alert_name, String alert_msg, String alert_time, String alert_subscriber, String alert_status, int alert_index, String alert_phone, String sub_image) {


        final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //lastEventIndex = sharedPreferences.getInt(Config.MESGRECVD_INDEX_SHARED_PREF, 1);
        curIndex = alert_index;
        System.out.println("Curindex--------------"+curIndex);


        if(curIndex>=1) {
            //readEventAtIndex(curIndex, sharedPreferences);
            System.out.println("Curindex--------------"+curIndex);


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Config.CURRENT_INDEX, curIndex);
            editor.commit();

            //final String alerttime = timestamp.toString();
            SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String email = sharedPreferences1.getString(Config.EMAIL_SHARED_PREF, "bmjo@ebirdonline.com");
            final String dialog_status = sharedPreferences1.getString(Config.DIALOG_CLOSE+"_"+curIndex, "");



            System.out.println("Dialog statusNext="+dialog_status);



            System.out.println("Dialog status="+dialog_status);

            //final String eventid = "acc501d0-9ff1-4338-8c0a-1a10df8b5d9e";


            if((dialog!=null)&&(dialog.isShowing())) dialog.dismiss();

            //final Dialog dialog = new Dialog(this);
            dialog = new Dialog(this);


            dialog.setContentView(R.layout.activity_alert);
            dialog.setTitle("Title...");
            final Button attend = (Button) dialog.findViewById(R.id.btnattend);
            Button viewalert = (Button) dialog.findViewById(R.id.btnviewalert);
            // set the custom dialog components - text, image and button
            textIndex = (TextView) dialog.findViewById(R.id.eventindex);
            textTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
            textEventDesc = (TextView) dialog.findViewById(R.id.eventRecvd);
            textnameSubs = (TextView) dialog.findViewById(R.id.eventSubName);
            textTime = (TextView) dialog.findViewById(R.id.eventTime);
            textEventName = (TextView) dialog.findViewById(R.id.eventTime);
            textPhone = (TextView) dialog.findViewById(R.id.eventPhone);
            subscriber = (ImageView) dialog.findViewById(R.id.subscriber);
            new DownLoadImageTask(subscriber).execute(sub_image);


            textIndex.setText("" + index);
            textTitle.setText(alert_name);
            //textEventDesc.setText(eventDesc + "---" + alertUUId);
            textEventDesc.setText(alert_msg);
            textnameSubs.setText(alert_subscriber);
            //textTime.setText(Long.toString(eventTime));
            /*if (alert_time > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm aa");
                Date dateTimeEvent = new Date();
                dateTimeEvent.setTime(alert_time);
                String timeStr = dateFormat.format(dateTimeEvent);
                textTime.setText(timeStr);
            } else {
                textTime.setText("Time not available");
            }*/
            //textEventName.setText(eventName);
            textTime.setText(alert_time);
            textPhone.setText(alert_phone);
            subscriber.setImageResource(getResources().getIdentifier(sub_image, "drawable", "com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare"));


            ImageButton dialogButtonClose = (ImageButton) dialog.findViewById(R.id.btnClose);
            // if button is clicked, close the custom dialog
            dialogButtonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            if(dialog_status.equals("open")) {
                attend.setBackgroundResource(R.drawable.buttonshape);
                attend.setEnabled(true);
                attend.setText("Close");
            }


            else if(dialog_status.equals("close")) {
                attend.setEnabled(false);
                attend.setBackgroundColor(Color.parseColor("#696969"));
                attend.setText("Attended");
            }
            else
            {
                attend.setEnabled(true);
                attend.setBackgroundResource(R.drawable.buttonshape);
                attend.setText("Attend");
            }


            ImageButton dialogButtonNext = (ImageButton) dialog.findViewById(R.id.btnNext);
            ImageButton dialogButtonPrev = (ImageButton) dialog.findViewById(R.id.btnPrev);
            // if button is clicked, close the custom dialog
            dialogButtonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    index++;


                        if(index<0 || index>list.size()-1)
                        {
                            System.out.println("EMPTY------------------");
                            index = list.size()-2;
                            Toast toast = Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }



                        else {


                            final AlertDetail alert_detail = (AlertDetail) list.get(index);

                            System.out.println("LIsting------------------" + alert_detail.alert_name);

                            openDialog(alert_detail.alert_name, alert_detail.alert_msg, alert_detail.alert_time, alert_detail.alert_subscriber, alert_detail.alert_status, alert_detail.alert_index, alert_detail.alert_phone, alert_detail.sub_image);

                        }

                        SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        final String dialog_status = sharedPreferences1.getString(Config.DIALOG_CLOSE+"_"+curIndex, "");

                        System.out.println("Dialog statusNext="+dialog_status);

                        if(dialog_status.equals("open")) {
                            attend.setBackgroundResource(R.drawable.buttonshape);
                            attend.setEnabled(true);
                            attend.setText("Close");
                        }

                        else if(dialog_status.equals("close")) {
                            attend.setEnabled(false);
                            attend.setBackgroundColor(Color.parseColor("#696969"));
                            attend.setText("Attended");
                        }
                        else
                        {
                            attend.setEnabled(true);
                            attend.setBackgroundResource(R.drawable.buttonshape);
                            attend.setText("Attend");
                        }
                    }

            });


            // if button is clicked, close the custom dialog
            dialogButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    index--;
                    //final String alerttime = timestamp.toString();

                        if(index<0 || index>list.size()-1)
                        {
                            System.out.println("EMPTY------------------");
                            index = 0;
                            Toast toast = Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }



                        else {


                            final AlertDetail alert_detail = (AlertDetail) list.get(index);

                            System.out.println("LIsting------------------" + alert_detail.alert_name);

                            openDialog(alert_detail.alert_name, alert_detail.alert_msg, alert_detail.alert_time, alert_detail.alert_subscriber, alert_detail.alert_status, alert_detail.alert_index, alert_detail.alert_phone, alert_detail.sub_image);

                        }


                        SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        final String dialog_status = sharedPreferences1.getString(Config.DIALOG_CLOSE+"_"+curIndex, "");

                        System.out.println("Dialog statusPrev="+dialog_status);

                        if(dialog_status.equals("open")) {
                            attend.setEnabled(true);
                            attend.setBackgroundResource(R.drawable.buttonshape);

                            attend.setText("Close");
                        }
                        else if(dialog_status.equals("close")) {
                            attend.setEnabled(false);
                            attend.setBackgroundColor(Color.parseColor("#696969"));
                            attend.setText("Attended");
                        }
                        else
                        {
                            attend.setEnabled(true);
                            attend.setBackgroundResource(R.drawable.buttonshape);
                            attend.setText("Attend");
                        }
                    }

            });



            // if button is clicked, close the custom dialog
            attend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    readEventAtIndex(curIndex, sharedPreferences);

                    updateStatus();

                }
            });

            viewalert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    alertDialogBuilder.setMessage("No Events reported in last 1 Hour");
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();*/

                    Toast toast=Toast.makeText(getApplicationContext(),"Yet to construct",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });
            // }


            // dialog.show();
        }else{

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("No Events reported in last 1 Hour");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }




    }


    void updateStatus()
    {
        //final String url = "http://178.128.165.237/php/api/eventopened.php";
        /*final Long alerttime = System.currentTimeMillis();

        SharedPreferences sharedPreferencesurl = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String url = sharedPreferencesurl.getString(Config.ALERT_OPENED, "");


        System.out.println("Alert url is ...."+url);

        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferencesurl.edit();

        //Adding values to editor
        editor.putInt(Config.CURRENT_INDEX, curIndex);
        editor.putString(Config.DIALOG_CLOSE+"_"+curIndex, "open");
        editor.commit();
        System.out.println("CurindexNext--------------"+curIndex);

        editor.putString(Config.ALERTID, alertUUId);
        editor.putString(Config.ALERTKEY, alertkeyval);
        editor.commit();*/
        SharedPreferences sharedPreferencesurl = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesurl.edit();
        System.out.println("check here ...."+alertUUId);
        //Adding values to editor
        editor.putInt(Config.CURRENT_INDEX, curIndex);


        editor.putString(Config.ALERTID, alertUUId);
        editor.putString(Config.ALERTKEY, alertkeyval);
        editor.commit();




        //Creating a string request
       /* RequestQueue queue = Volley.newRequestQueue(this);


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
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                //String jsonData ="{\"eventKey:\"+alertid+,\"timeStamp\":alerttime,\"careTakerEmail\":email,\"actionType\":alertstatus,\"actionDesc\":note}";
                //String jsonData = String.format("{\"eventKey:\"%s,\"timeStamp\":%s,\"careTakerEmail\":%s,\"actionType\":%s,\"actionDesc\":%s}",alertid,alerttime,email,alertstatus,note) ;
                String jsonData = "";
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("alertUUId", alertUUId); // Set the first name/pair
                    jsonObj.put("openedTime", alerttime);
                    jsonObj.put("eventKey", alertkeyval);
                    jsonData = jsonObj.toString();

                    System.out.println("Alert Time="+alerttime);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return jsonData.getBytes();

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();*/


        Intent intent = new Intent(SubscriberActivity.this, AlertActivity.class);
        System.out.println("ALERT_OPENED.............."+alertUUId);
        intent.putExtra("alertkey",alertkeyval);
        intent.putExtra("alertUUId",alertUUId);
        dialog.dismiss();
        startActivity(intent);
    }

    public void loadSubscribers() {

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "bmjo1@ebirdonline.com");
            System.out.println("current caregiver"+email);
            final String url = sharedPreferences.getString(Config.URL_SUBSCRIBER, "");
            final String topic = sharedPreferences.getString(Config.TOPIC, "");

            System.out.println("Working..."+url);
            System.out.println("Topic..."+topic);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            CommonDataArea.subscribersList = new ArrayList<>();
            final String requestBody = jsonBody.toString();

            //System.out.println("IP.."+Config.URL_SUBSCRIBER);
            /*
             * Creating a String Request
             * The request type is GET defined by first parameter
             * The URL is defined in the second parameter
             * Then we have a Response Listener and a Error Listener
             * In response listener we will get the JSON response as a String
             * */
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //converting the string to json array object
                                //JSONObject sub=new JSONObject(response);
                                //JSONArray array = sub.getJSONArray("records");
                                //JSONArray jArr = jObj.getJSONArray("list");
                                //traversing through all the object
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject subscriber = array.getJSONObject(i);
                                    SharedPreferences sharedPreferences = SubscriberActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                    //Creating editor to store values to shared preferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    //Adding values to editor
                                    editor.putBoolean(Config.MESGRECVD_SHARED_PREF, true);
                                    editor.putString(Config.MESGRECVD_EMAIL, subscriber.getString("emailId"));
                                    editor.putString(Config.MESGRECVD_SUBSCRIBER, subscriber.getString("name"));
                                    editor.putString(Config.MESGRECVD_PHONE, subscriber.getString("phone"));
                                    editor.putString(Config.MESGRECVD_IMG_URL, subscriber.getString("imageurl"));

                                    editor.commit();

                                    //adding the product to product list
                                    CommonDataArea.subscribersList.add(new Subscribers(
                                            subscriber.getString("name"),
                                            subscriber.getString("phone"),
                                            subscriber.getString("imageurl"),
                                            subscriber.getString("emailId")
                                    ));
                                }

                                //creating adapter object and setting it to recyclerview
                                SubscribersAdapter adapter = new SubscribersAdapter(SubscriberActivity.this, CommonDataArea.subscribersList);
                                adapter.subscriberActivity =SubscriberActivity.this;
                                recyclerView.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }


            };
            ;
            //adding our stringrequest to queue
            Volley.newRequestQueue(this).add(stringRequest);
        } catch (Exception exp) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private Database_Helper getHelper() {
        if (database_helper == null) {
            database_helper = OpenHelperManager.getHelper(this, Database_Helper.class);
        }
        return database_helper;
    }

    @Override
    public void onBackPressed() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void logout() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();
                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        editor.putBoolean(Config.SETTINGS_RESUME, false);
                        editor.putBoolean(Config.SETTINGS_SHARED_PREF, false);
                        editor.putString(Config.TOPIC, "");
                        editor.putString(Config.SERVER_IP, "");
                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");
                        editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF,0);
                        CommonDataArea.subscribersList=null;
                        //Saving the sharedpreferences
                        editor.commit();

                        Intent intent = new Intent(SubscriberActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        //Starting login activity
                        //finish();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(1);


                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void settings() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialogBuilder.setMessage("Are you sure you want to change settings?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();
                        //Puting the value false for loggedin
                        //editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        //editor.putBoolean(Config.SETTINGS_SHARED_PREF, false);
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                        editor.putBoolean(Config.SETTINGS_RESUME, true);


                        //Putting blank value to email
                        //editor.putString(Config.EMAIL_SHARED_PREF, "");
                        editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF,1);
                        CommonDataArea.subscribersList=null;
                        CommonDataArea.showSettingDlg = true;
                        //Saving the sharedpreferences
                        editor.commit();

                        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

                        //Fetching the boolean value form sharedpreferences
                        settingsIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        System.out.println("LoginSettings"+settingsIn);

                        Intent intent = new Intent(SubscriberActivity.this, SettingsActivity.class);


                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        startActivity(intent);
                        //Starting login activity
                        //finish();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(1);


                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            //calling logout method when the logout button is clicked
            logout();
        }
        if(id == R.id.eventDetails){
            //checkLastEventReported(null);

            Intent intent = new Intent(SubscriberActivity.this, DisplayAlertActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            //startActivity(new Intent(SubscriberActivity.this, DisplayAlertActivity.class));
        }

        if(id == R.id.register){

            Intent intent = new Intent(SubscriberActivity.this, SubscriberRegistrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            //startActivity(new Intent(SubscriberActivity.this, SubscriberRegistrationActivity.class));
        }
        if (id == R.id.wifisettings) {

            Intent intent = new Intent(SubscriberActivity.this, WifiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            //startActivity(new Intent(SubscriberActivity.this, WifiActivity.class));

        }
        if (id == R.id.settings) {
            //calling settings when  button is clicked
            //Intent intent = new Intent(SubscriberActivity.this, SettingsActivity.class);
            //startActivity(intent);
            settings();

        }
        return super.onOptionsItemSelected(item);
    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            if ((result != null)) {
                imageView.setImageBitmap(result);
            }
            else
            {
                imageView.setImageResource(R.drawable.man);
            }
        }
    }

}