package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;
import com.iorbit_tech.healthcare.caretakerapp.utils.LogWriter;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;
    private AppCompatButton buttonLogin, buttonRegister;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        String body = intent.getStringExtra("Body");
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {

            title = bundle.getString("title");
            body = bundle.getString("body");
        }
        if((title!=null)&&(body!=null)){
            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            //Creating editor to store values to shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //Adding values to editor
            editor.putBoolean(Config.MESGRECVD_SHARED_PREF, true);
            editor.putString(Config.MESGTITLE_SHARED_PREF, title);
            editor.putString(Config.MESGBODY_SHARED_PREF, body);
            editor.commit();

        }
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Initializing views
        editTextEmail = (EditText) findViewById(R.id.input_email);
        editTextPassword = (EditText) findViewById(R.id.input_password);

        buttonLogin = (AppCompatButton) findViewById(R.id.btn_login);

            buttonRegister = (AppCompatButton) findViewById(R.id.btn_register);

        //Adding click listener
        buttonLogin.setOnClickListener(this);

            buttonRegister.setOnClickListener(this);
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/ihealthcare");//To recive application wide messages
            LogWriter.writeLog("Firebase called", "Login");
        }catch(Exception exp){
            com.iorbit_tech.healthcare.caretakerapp.utils.LogWriter.writeLogException("MainActivity",exp);
        }





    }

   @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, SubscriberActivity.class);
            startActivity(intent);
            finish();;
        }
    }

    private void login(){
        //Getting values from edit texts

            //Getting values from edit texts
            final String email = editTextEmail.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();

            //Creating a string request
            /*StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("REsponse"+response);
                            //If we are getting success from server
                            if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                                //Creating a shared preference
                                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                //Adding values to editor
                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                                editor.putString(Config.EMAIL_SHARED_PREF, email);

                                //Saving values to editor
                                editor.commit();

                                //Starting profile activity
                                Intent intent = new Intent(LoginActivity.this, SubscriberActivity.class);
                                startActivity(intent);
                            }else{
                                //If the server response is not success
                                //Displaying an error message on toast
                                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put(Config.KEY_EMAIL, email);
                    params.put(Config.KEY_PASSWORD, password);
                    JSONObject obj=new JSONObject(params);
System.out.println(obj);

                    //returning parameter
                    return params;
                }
            };

            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);*/



        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        JSONObject json = new JSONObject();

        try{
            final SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String loginurl = sharedPreferences1.getString(Config.LOGIN_URL, "");

            //String loginurl = "https://healthcare.iorbit-tech.com/php/api/careGiverLogin.php";
            System.out.println("Login detail value"+loginurl);
            HttpPost post = new HttpPost(loginurl);
            post.setHeader("Content-type", "application/json");
            json.put("subscriber", email);
            json.put("pass", password);
            StringEntity se = new StringEntity( json.toString());
            Log.v("json_text", json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);
            /*Checking response */
            if(response!=null){
                String temp = EntityUtils.toString(response.getEntity()); //Get the data in the entity
                Log.v("response", temp);

                if(temp.contains(Config.LOGIN_SUCCESS)){
                    //Creating a shared preference
                   SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                    //Creating editor to store values to shared preferences
                   SharedPreferences.Editor editor = sharedPreferences.edit();

                    //Adding values to editor
                    editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                    editor.putString(Config.EMAIL_SHARED_PREF, email);

                    //Saving values to editor
                    editor.commit();
                    LogWriter.writeLog("LoginSuccess", "Login");
                    //Starting profile activity
                    Intent intent = new Intent(LoginActivity.this, SubscriberActivity.class);
                    startActivity(intent);
                }else{
                    //If the server response is not success
                    //Displaying an error message on toast
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                }
            }}
        catch(Exception e){
            e.printStackTrace();
            //createDialog("Error", "Cannot Estabilish Connection");
        }



        }

        @Override
        public void onClick(View v) {
            //Calling the login function


            switch (v.getId()) {

                case R.id.btn_login:
                    login();
                    break;

                case R.id.btn_register:
                    Intent intent = new Intent(LoginActivity.this, CareGiverRegisterActivity.class);
                    startActivity(intent);
                    break;



                default:
                    break;
            }
        }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        String title = intent.getStringExtra("Title");
        String body = intent.getStringExtra("Body");
        setIntent(intent);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(body);
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.show();

    }
    }