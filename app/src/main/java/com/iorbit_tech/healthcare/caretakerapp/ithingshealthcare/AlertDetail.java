package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import com.j256.ormlite.field.DatabaseField;

public class AlertDetail {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;
    @DatabaseField(columnName = "alert_uuid")
    public String alert_uuid;
    @DatabaseField(columnName = "event_key")
    public String event_key;
    @DatabaseField(columnName = "alert_name")
    public String alert_name;
    @DatabaseField(columnName = "alert_msg")
    public String alert_msg;
    @DatabaseField(columnName = "alert_time")
    public String alert_time;
    @DatabaseField(columnName = "alert_subscriber")
    public String alert_subscriber;
    @DatabaseField(columnName = "alert_status")
    public String alert_status;
    @DatabaseField(columnName = "alert_index")
    public int alert_index;
    @DatabaseField(columnName = "alert_phone")
    public String alert_phone;
    @DatabaseField(columnName = "sub_image")
    public String sub_image;
    @DatabaseField(columnName = "alert_email")
    public String alert_email;
    public AlertDetail(){
    }
    public AlertDetail(final String alert_uuid, final String alert_name, final String alert_msg, final String alert_time, final String alert_subscriber, final String alert_status ){
        this.alert_uuid = alert_uuid;
        this.alert_name = alert_name;
        this.alert_msg = alert_msg;
        this.alert_time = alert_time;
        this.alert_subscriber = alert_subscriber;
        this.alert_status = alert_status;

    }
}