package com.iorbit_tech.healthcare.caretakerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.SubscriberActivity;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.Subscribers;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.SubscribersAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CommonFunctionArea {

    public static void loadSubscribers(final Context context) {

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "bmjo1@ebirdonline.com");
            final String url = sharedPreferences.getString(Config.URL_SUBSCRIBER, "");
            final String topic = sharedPreferences.getString(Config.TOPIC, "");

            System.out.println("Working..."+url);
            System.out.println("Topic..."+topic);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            CommonDataArea.subscribersList = new ArrayList<>();
            final String requestBody = jsonBody.toString();

            System.out.println("IP.."+Config.URL_SUBSCRIBER);
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
                                    SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
            Volley.newRequestQueue(context).add(stringRequest);
        } catch (Exception exp) {

        }
    }
}
