package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CareGiverRegisterActivity extends AppCompatActivity implements View.OnClickListener {

        //View Objects
        private Button buttonRegister;
        private EditText textViewEmail, textViewName, textViewPhone;
        String careemail, carename, carephone;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //View objects

            buttonRegister = (Button) findViewById(R.id.btn_register);
            textViewEmail = (EditText) findViewById(R.id.input_email);
            textViewName = (EditText) findViewById(R.id.input_name);
            textViewPhone = (EditText) findViewById(R.id.input_phone);
            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String url = sharedPreferences.getString(Config.CAREGIVER_REGISTER, "");

            System.out.println("hhhhhhhh"+url);
            //intializing scan object






            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendCareGiverDetails();
                }
            });
        }



        void sendCareGiverDetails()
        {
            if(textViewEmail.getText().toString().length()==0){
                textViewEmail.setError("First name not entered");
                textViewEmail.requestFocus();
            }
            if(textViewName.getText().toString().length()==0){
                textViewName.setError("Last name not entered");
                textViewName.requestFocus();
            }

            if(textViewPhone.getText().toString().length()==0){
                textViewPhone.setError("Username is Required");
                textViewPhone.requestFocus();
            }

            else {
                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String url = sharedPreferences.getString(Config.CAREGIVER_REGISTER, "");
                System.out.println("Careurl" + url);
                careemail = textViewEmail.getText().toString();

                carename = textViewName.getText().toString();
                carephone = textViewPhone.getText().toString();
                RequestQueue queue = Volley.newRequestQueue(this);


                //final String url = "http://178.128.165.237/php/api/smart/registerAppDevice.php";
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject caregiver = new JSONObject(response);
                                    JSONArray array = caregiver.getJSONArray("responses");

                                    for (int i = 0; i < array.length(); i++) {

                                        //getting product object from json array
                                        JSONObject care = array.getJSONObject(i);


                                        if (care.has("error")) {
                                            String val = care.getString("error");
                                            if (val.contains("Already exist. Please register a new caregiver")) {
                                                displayError();

                                            }
                                        }

                                        if (care.has("message")) {

                                            String val1 = care.getString("message");

                                            if (val1.contains("Please check your email. Verify your email before proceeding.")) {
                                                displaySuccess();
                                            }
                                        }

                                    }
                                } catch (JSONException e) {
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
                            jsonObj.put("email", careemail); // Set the first name/pair
                            jsonObj.put("name", carename);
                            jsonObj.put("phone", carephone);


                            jsonData = jsonObj.toString();
                            System.out.println("caregiver data send");


                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        return jsonData.getBytes();

                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                queue.start();


                //textViewId.setText("");
                //textViewName.setText("");

            }
        }

        void displayError()
        {
            Toast.makeText(this, "Already exist please register new caregiver", Toast.LENGTH_LONG).show();
        }

        void displaySuccess()
        {
            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show();
            //textViewId.setText("");
            //textViewTag.setText("");
            Intent intent = new Intent(com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.CareGiverRegisterActivity.this, LoginActivity.class);


            startActivity(intent);
        }




        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            onBackPressed();
            return true;
        }


    @Override
    public void onClick(View v) {

    }
}

