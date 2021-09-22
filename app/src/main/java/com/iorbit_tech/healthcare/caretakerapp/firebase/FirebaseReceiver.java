package com.iorbit_tech.healthcare.caretakerapp.firebase;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.AlertActivity;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.AlertDetail;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.Database_Helper;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.R;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.Subscribers;
import com.iorbit_tech.healthcare.caretakerapp.utils.CommonDataArea;
import com.iorbit_tech.healthcare.caretakerapp.utils.CommonFunctionArea;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.SubscriberActivity;
import com.iorbit_tech.healthcare.caretakerapp.utils.LogWriter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;


public class FirebaseReceiver extends FirebaseMessagingService {

    static int num = 0;
    private Dao<AlertDetail, Integer> alertDao;

    private Database_Helper database_helper = null;
    public static final String CHANNEL_ID = "com.iorbit_tech.healthcare.caretakerapp";
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {


            //notification

           /* notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Setting up Notification channels for android O and above
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels();
            }
            int notificationId = new Random().nextInt(60000);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ithings_logo)  //a resource for your custom small icon
                    .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
                    .setContentText(remoteMessage.getData().get("message")) //ditto
                    .setAutoCancel(true)  //dismisses the notification on click
                    .setSound(defaultSoundUri);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId /* ID of notification , notificationBuilder.build());

            LogWriter.writeLog("MesgRecvd", "FireBaseMesg");*/



            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            //Fetching the boolean value form sharedpreferences
            boolean loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
            String loguseremail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "");
            CommonDataArea.popup = sharedPreferences.getBoolean(Config.DIASBLE_POP_SHARED_PREF,true);

            //if ((remoteMessage.getFrom().contains("/topics/ihealthcare") )&& loggedIn && (remoteMessage.getFrom().contains(loguseremail) )) {
            if (remoteMessage.getFrom().contains("/topics/ihealthcare")) {
                LogWriter.writeLog("Inside function", "FireBaseMesg");
                System.out.println("Alert Details" + remoteMessage.getData());






                Map<String, String> data = remoteMessage.getData();
                final String alertUUId = data.get("alertUUId");
                final String eventkey = data.get("eventKey");
                alertDao = getHelper().getInformationDao();
                QueryBuilder<AlertDetail, Integer> queryBuilder = alertDao.queryBuilder();
                List list;
                queryBuilder.where().eq("event_key", eventkey);
                list = queryBuilder.query();
                if (list.size() > 0) {
                    System.out.println("Already exist");
                } else {
                    String title = data.get("title");
                    String bodytxt = data.get("body");
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ithings_logo)
                            .setContentTitle(data.get("title"))
                            .setContentText(data.get("body"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            //.setSound(Uri.parse("android.resource://com.roommanagement.app/" + R.raw.burgleralarm));





                    String body = bodytxt.substring(bodytxt.indexOf("->") + 2);


                    Map<String, String> values = remoteMessage.getData();
                    String dataMesg = values.get("title");

                    LogWriter.writeLog("Meta", dataMesg);
                    final Long alerttime = System.currentTimeMillis();

                    //final String alerttime = timestamp.toString();
                    SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    final String email = sharedPreferences1.getString(Config.EMAIL_SHARED_PREF, "bmjo@ebirdonline.com");
                    //final String eventid = "acc501d0-9ff1-4338-8c0a-1a10df8b5d9e";

                    final String url = sharedPreferences1.getString(Config.ALERT_DELIVERED, "");


                    //final String url = "http://178.128.165.237/php/api/eventdelivered.php";
                    System.out.println("ALERT_DELIVERED is ......." + url);

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
                                jsonObj.put("alertUUId", alertUUId); // Set the first name/pair
                                jsonObj.put("deliveredTime", alerttime);
                                jsonData = jsonObj.toString();

                                System.out.println("Alert Time=" + alerttime);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            return jsonData.getBytes();

                        }
                    };
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    queue.start();

                    /*if (CommonDataArea._from_background) {
                        startActivity(new Intent(this, SubscriberActivity.class));
                    }


                    if (checkMesgBelogsToMySubscribers(values.get("email"))) {
                        saveEventDetails(title, body, values);
                        processMetaMesg(title, body);
                    }

                    return;*/
                    LogWriter.writeLog("FM","Title:-"+title+"Body:-"+body);


                    LogWriter.writeLog("Meta", dataMesg);

                    if(CommonDataArea.subscribersList==null) CommonFunctionArea.loadSubscribers(getApplicationContext());
                    if(checkMesgBelogsToMySubscribers(values.get("email"))) {
                        LogWriter.writeLog("FM","Message for My subscribers");
                        saveEventDetails(title, body, values);
                        processMetaMesg(title, body);
                    }

                    return;

                }
                }
            } catch(Exception exp){
                LogWriter.writeLogException("MesgRecvd", exp);
            }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "123";
        String adminChannelDescription = "123";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }





    boolean checkMesgBelogsToMySubscribers(String subsEmail){
        for(Subscribers subs  : CommonDataArea.subscribersList) {
            if (subs.getEmail().contains(subsEmail)) return true;
        }
        return  false;
    }
    public void processMetaMesg(String title, String body) {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        //Fetching the boolean value form sharedpreferences
        boolean loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        /*ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(this, SubscriberActivity.class);

            intent.putExtra("Title", title);
            intent.putExtra("Body", body);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
        }*/
        //If we will get true
        if(loggedIn) {
            LogWriter.writeLog("FM", "Care taker logged in");

            System.out.println("Beep Val="+CommonDataArea.beep);
            if (CommonDataArea.beep) {

                LogWriter.writeLog("FM", "Beeping");
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 2000);
            } else {
                LogWriter.writeLog("FM", "Beep not enabled");
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MIN_VOLUME);
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 2000);
            }

            if (CommonDataArea.popup) {
                //We will start the Profile Activity
                LogWriter.writeLog("FM", "Bring activity to front");
                Intent intent = new Intent(this, SubscriberActivity.class);

                intent.putExtra("Title", title);
                intent.putExtra("Body", body);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            } else {
                LogWriter.writeLog("FM", "Popup not enabled");
            }
        }


    }
    //todo saving it in shared pref now. But later it can be saved in a
    //database like orm lite or sqllite
    void saveEventDetails(String title,String body, Map<String, String> values){


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String purl = sharedPreferences.getString(Config.SERVER_IP, "healthcare.iorbit-tech.com");
        String photourl ="http://"+purl+"/php/photos/"+values.get("imageURL");
        System.out.println("Photo detail value"+photourl);

        System.out.println("alert detail value"+values.get("alertUUId"));

        int indexEvent = sharedPreferences.getInt(Config.MESGRECVD_INDEX_SHARED_PREF,1);
        ++indexEvent;
        if(indexEvent>50) indexEvent=1;
        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Adding values to editor
        editor.putBoolean(Config.MESGRECVD_SHARED_PREF+"_"+indexEvent, true);
        editor.putString(Config.MESGTITLE_SHARED_PREF+"_"+indexEvent, title);
        editor.putString(Config.MESGBODY_SHARED_PREF+"_"+indexEvent, body);
        //editor.putString(Config.DIALOG_CLOSE+"_"+indexEvent, "open");
        editor.putLong(Config.MESGRECVD_TIME_SHARED_PREF+"_"+indexEvent, System.currentTimeMillis());
        editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF, indexEvent);
        editor.putString(Config.ALERT_KEY+"_"+indexEvent,values.get("eventKey"));

        editor.putString(Config.MESGRECVD_EMAIL+"_"+indexEvent,values.get("email"));
        editor.putString(Config.MESGRECVD_EVENTNAME+"_"+indexEvent,values.get("eventName"));
        editor.putString(Config.MESGRECVD_PHONE+"_"+indexEvent,values.get("phone"));
        //editor.putString(Config.MESGRECVD_IMG_URL+"_"+indexEvent,values.get("imageURL"));
        editor.putString(Config.MESGRECVD_IMG_URL+"_"+indexEvent,photourl);
        editor.putString(Config.MESGRECVD_SUBSCRIBER+"_"+indexEvent,values.get("subscriberName"));
        editor.putString(Config.ALERT_KEY+"_"+indexEvent,values.get("eventKey"));
        editor.putString(Config.ALERT_UUID+"_"+indexEvent,values.get("alertUUId"));

        editor.commit();




        final AlertDetail alert_detail = new AlertDetail();
        alert_detail.alert_uuid = values.get("alertUUId");
        alert_detail.event_key = values.get("eventKey");
        // alert_detail.alert_name = title;
        String eventName = values.get("eventName");
        int indexColun = eventName.indexOf(':');
        if (eventName.length() > indexColun)
            eventName = eventName.substring(indexColun + 1);
        alert_detail.alert_name = eventName;
        alert_detail.alert_msg = body;
        alert_detail.alert_phone= values.get("phone");
        alert_detail.alert_email= values.get("email");
        alert_detail.sub_image = photourl;

        alert_detail.alert_time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        alert_detail.alert_subscriber = values.get("subscriberName");
        alert_detail.alert_status = "attend";
        alert_detail.alert_index = indexEvent;


        try {
            final Dao<AlertDetail, Integer> alertDao = getHelper().getInformationDao();
            alertDao.create(alert_detail);
            //reset();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    /* private void reset()
     {
         addName.setText("");
         addEmail.setText("");
     }*/
    private Database_Helper getHelper() {
        if (database_helper == null) {
            database_helper = OpenHelperManager.getHelper(this,Database_Helper.class);
        }
        return database_helper;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database_helper != null) {
            OpenHelperManager.releaseHelper();
            database_helper = null;
        }
    }
}

