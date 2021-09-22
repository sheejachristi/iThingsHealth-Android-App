package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iorbit_tech.healthcare.caretakerapp.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class SubscriberRegistrationActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {

    Button getLocationBtn;
    TextView locationText;

    LocationManager locationManager;
    Location location;
    double latitude, longitude;

    private Button buttonRegister;
    private EditText textViewEmail, textViewName, textViewPhone;
    String careuseremail, careusername, careuserphone, lat, lon, street1, street2, pincode, city, state, country, caregiveremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_subscriber_registration);

        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        locationText = (TextView)findViewById(R.id.locationText);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        buttonRegister = (Button) findViewById(R.id.btn_register);
        textViewEmail = (EditText) findViewById(R.id.input_email);
        textViewName = (EditText) findViewById(R.id.input_name);
        textViewPhone = (EditText) findViewById(R.id.input_phone);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String url = sharedPreferences.getString(Config.CAREGIVER_REGISTER, "");

        System.out.println("hhhhhhhh"+url);
        //intializing scan object









        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendCareUserDetails();
            }
        });
    }

    void sendCareUserDetails()
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
            String url = sharedPreferences.getString(Config.CAREUSER_REGISTER, "");
            System.out.println("Careurl" + url);
            careuseremail = textViewEmail.getText().toString();

            careusername = textViewName.getText().toString();
            careuserphone = textViewPhone.getText().toString();
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
                                        if (val.contains("Already exist. Please register a new subscriber")) {
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
                    final SharedPreferences sharedPreferences1 = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    caregiveremail = sharedPreferences1.getString(Config.EMAIL_SHARED_PREF, "");
                    System.out.println("caregiver email"+caregiveremail);
                    String jsonData = "";
                    try {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("email", careuseremail); // Set the first name/pair
                        jsonObj.put("caregiveremail", caregiveremail);
                        jsonObj.put("name", careusername);
                        jsonObj.put("phone", careuserphone);
                        jsonObj.put("latitude", lat);
                        jsonObj.put("longitude", lon);
                        jsonObj.put("street1", street1);
                        jsonObj.put("street2", street2);
                        jsonObj.put("city", city);
                        jsonObj.put("state", state);
                        jsonObj.put("country", country);
                        jsonObj.put("pincode", pincode);
                        jsonData = jsonObj.toString();
                        System.out.println("careuser data send");


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
        Toast.makeText(this, "Already exist. Please register a new careuser", Toast.LENGTH_LONG).show();
    }

    void displaySuccess()
    {
        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show();
        //textViewId.setText("");
        //textViewTag.setText("");
        Intent intent = new Intent(com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.SubscriberRegistrationActivity.this, SubscriberActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    void getLocation() {
        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);

            if (locationManager != null) {
                System.out.println("Right");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    System.out.println("Location");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    lat = Double.toString(latitude);
                    lon = Double.toString(longitude);
                    System.out.println("lat"+location.getLatitude());
                    System.out.println("lon"+location.getLongitude());

                    locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

                    try {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                                addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        city = addresses.get(0).getLocality();
                         state = addresses.get(0).getAdminArea();
                         country = addresses.get(0).getCountryName();
                         pincode = addresses.get(0).getPostalCode();
                        street1 = addresses.get(0).getFeatureName(); // Only if available else return NULL

                        System.out.println("street"+address+"city"+city+"state"+state+"country"+country+"postalcode"+pincode+"knownname"+street1);

                    }catch(Exception e)
                    {

                    }

                }
                else
                {
                    System.out.println("wrong");
                }
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(SubscriberRegistrationActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}