package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.iorbit_tech.healthcare.caretakerapp.utils.CommonDataArea;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;
import com.iorbit_tech.healthcare.caretakerapp.utils.LogWriter;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextServerIP;
    private EditText editTextTopic;
    private AppCompatButton buttonSubmit;
    private boolean settingsIn = false;
    private boolean settingsResume = false;
    SubscriberActivity subscribers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Initializing views
        editTextServerIP = (EditText) findViewById(R.id.input_serverip);
        editTextTopic = (EditText) findViewById(R.id.input_topic);

        checkIp();

        buttonSubmit = (AppCompatButton) findViewById(R.id.btn_settings_submit);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences

        String sip = sharedPreferences.getString(Config.SERVER_IP,"");
        String stopic = sharedPreferences.getString(Config.TOPIC,"");

        editTextServerIP.setText(sip);
        editTextTopic.setText(stopic);

        //Adding click listener
        buttonSubmit.setOnClickListener(this);

        final CheckBox beepCheck = (CheckBox) findViewById(R.id.disableSound);
        if(CommonDataArea.beep) beepCheck.setChecked(true); else  beepCheck.setChecked(false);
        beepCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beepCheck.isChecked()) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean(Config.DISABLE_BEEP_SHARED_PREF, true);
                    CommonDataArea.beep = true;
                    editor.commit();
                }else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    CommonDataArea.beep = false;
                    editor.putBoolean(Config.DISABLE_BEEP_SHARED_PREF, false);
                    editor.commit();
                }
            }
        });

        final CheckBox popCheck = (CheckBox) findViewById(R.id.disablePop);
        if(CommonDataArea.popup) popCheck.setChecked(true); else popCheck.setChecked(false);
        popCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popCheck.isChecked()){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean(Config.DIASBLE_POP_SHARED_PREF, true);
                    CommonDataArea.popup = true;
                    editor.commit();
                }else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean(Config.DIASBLE_POP_SHARED_PREF, false);
                    CommonDataArea.popup = false;
                    editor.commit();
                }

            }
        });
    }


    public void checkIp()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences


        settingsResume = sharedPreferences.getBoolean(Config.SETTINGS_RESUME, false);

        System.out.print("ChheckWork"+settingsResume);

        if (settingsResume) {

            editTextServerIP.setEnabled(false);

        }
    }


   @Override
    protected void onResume() {
        super.onResume();

        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        settingsIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        settingsResume = sharedPreferences.getBoolean(Config.SETTINGS_RESUME, false);
        String sip = sharedPreferences.getString(Config.SERVER_IP,"");
        String stopic = sharedPreferences.getString(Config.TOPIC,"");
        System.out.println("Settingsip......"+sip);
        System.out.println("Settingslog......"+settingsIn);
        System.out.println("Settingstopic......"+stopic);

        if(!CommonDataArea.showSettingDlg) {
            //If we will get true
            if (settingsIn) {
                //We will start the Profile Activity


                //If we will get true
                editTextServerIP.setEnabled(false);

                    editTextServerIP.setText(sip);
                    editTextTopic.setText(stopic);
                Intent intent = new Intent(SettingsActivity.this, SubscriberActivity.class);
                startActivity(intent);
                finish();

            }
        }
        CommonDataArea.showSettingDlg=false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        final CheckBox beepCheck = (CheckBox) findViewById(R.id.disableSound);
        if(CommonDataArea.beep) beepCheck.setChecked(true); else  beepCheck.setChecked(false);
        beepCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beepCheck.isChecked()) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean(Config.DISABLE_BEEP_SHARED_PREF, true);
                    CommonDataArea.beep = true;
                    editor.commit();
                }else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    CommonDataArea.beep = false;
                    editor.putBoolean(Config.DISABLE_BEEP_SHARED_PREF, false);
                    editor.commit();
                }
            }
        });

        final CheckBox popCheck = (CheckBox) findViewById(R.id.disablePop);
        if(CommonDataArea.popup) popCheck.setChecked(true); else popCheck.setChecked(false);
        popCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popCheck.isChecked()){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean(Config.DIASBLE_POP_SHARED_PREF, true);
                    CommonDataArea.popup = true;
                    editor.commit();
                }else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean(Config.DIASBLE_POP_SHARED_PREF, false);
                    CommonDataArea.popup = false;
                    editor.commit();
                }

            }
        });
        SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"");

        System.out.println("Testing----------------"+email);
        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Adding values to editor
        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
        editor.putString(Config.EMAIL_SHARED_PREF, email);

        //Saving values to editor
        editor.commit();

        //Starting profile activity
        Intent intent = new Intent(SettingsActivity.this, SubscriberActivity.class);
        startActivity(intent);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString(Config.SERVER_IP, url);

       /* Intent intent = new Intent(SettingsActivity.this, SubscriberActivity.class);
        startActivity(intent);*/
        return true;
    }

    @Override
    public void onClick(View v)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
        settingsIn = preferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        String sip = preferences.getString(Config.SERVER_IP,"");
        String stopic = preferences.getString(Config.TOPIC,"");
        String email = preferences.getString(Config.EMAIL_SHARED_PREF,"");
        System.out.println("is caregiver"+email);
        System.out.println("Settings......"+settingsIn);
        //If we will get true
        if (settingsIn) {

        editTextServerIP.setText(sip);
        editTextTopic.setText(stopic);
        editTextServerIP.setEnabled(false);

        String serverip = editTextServerIP.getText().toString();
        String topic = editTextTopic.getText().toString();



        LogWriter.writeLog("topic", "Settings");
        // TODO Auto-generated method stub
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(Config.SETTINGS_SHARED_PREF, true);


        //Saving values to editor

        //Puting the value for server ip
        editor.putString(Config.SERVER_IP, serverip);
        editor.putString(Config.LOGIN_URL, "http://"+serverip+"/php/api/careGiverLogin.php");
        editor.putString(Config.URL_SUBSCRIBER, "http://"+serverip+"/php/api/getAssignedCareUserListForCareGiver.php");
        editor.putString(Config.ALERT_URL, "http://"+serverip+"/php/api/createEventActionNote.php");
        editor.putString(Config.SCAN_URL, "http://"+serverip+"/php/api/smart/registerAppDevice.php");
            editor.putString(Config.CAREGIVER_REGISTER, "http://"+serverip+"///php/api/smart/caregiver-register.php");

            editor.putString(Config.CAREUSER_REGISTER, "http://"+serverip+"///php/api/smart/registerCareuser.php");

        editor.putString(Config.ALERT_DELIVERED, "http://"+serverip+"/php/api/eventdelivered.php");
        editor.putString(Config.ALERT_OPENED, "http://"+serverip+"/php/api/eventopened.php");

        editor.putString(Config.TOPIC, topic);
        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
        //Putting blank value to email
        editor.putString(Config.EMAIL_SHARED_PREF, email);
        editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF,0);


        //Saving the sharedpreferences
        editor.commit();

        final SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString(Config.ALERT_DELIVERED, "");
        String url = sharedPreferences.getString(Config.URL_SUBSCRIBER, "");
        String loginurl = sharedPreferences.getString(Config.LOGIN_URL, "");
        String scanurl = sharedPreferences.getString(Config.SCAN_URL, "");
        System.out.println("ip value"+ip);
        System.out.println("url value"+loginurl);
        System.out.println("url value"+url);



        //Fetching the boolean value form sharedpreferences

                //We will start the Profile Activity

                System.out.println("Settings123......"+settingsIn);
                Intent intent = new Intent(SettingsActivity.this, SubscriberActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
                CommonDataArea.showSettingDlg=false;
                ;
            }
            else {

                System.out.println("Settingsabc......"+settingsIn);
            editTextServerIP.setEnabled(true);
            String serverip = editTextServerIP.getText().toString();
            String topic = editTextTopic.getText().toString();

            LogWriter.writeLog("topic", "Settings");
            // TODO Auto-generated method stub
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
            //Creating editor to store values to shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean(Config.SETTINGS_SHARED_PREF, true);


            //Saving values to editor

            //Puting the value for server ip
            editor.putString(Config.SERVER_IP, serverip);
            editor.putString(Config.LOGIN_URL, "http://"+serverip+"/php/api/careGiverLogin.php");
            editor.putString(Config.URL_SUBSCRIBER, "http://"+serverip+"/php/api/getAssignedCareUserListForCareGiver.php");
            editor.putString(Config.ALERT_URL, "http://"+serverip+"/php/api/createEventActionNote.php");
            editor.putString(Config.SCAN_URL, "http://"+serverip+"/php/api/smart/registerAppDevice.php");

            editor.putString(Config.CAREGIVER_REGISTER, "http://"+serverip+"///php/api/smart/caregiver-register.php");

            editor.putString(Config.CAREUSER_REGISTER, "http://"+serverip+"///php/api/smart/registerCareuser.php");

            editor.putString(Config.ALERT_DELIVERED, "http://"+serverip+"/php/api/eventdelivered.php");
            editor.putString(Config.ALERT_OPENED, "http://"+serverip+"/php/api/eventopened.php");

            editor.putString(Config.TOPIC, topic);
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
            //Putting blank value to email
            editor.putString(Config.EMAIL_SHARED_PREF, "");
            editor.putInt(Config.MESGRECVD_INDEX_SHARED_PREF,0);


            //Saving the sharedpreferences
            editor.commit();

                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
    }
}