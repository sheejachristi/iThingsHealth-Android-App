package com.iorbit_tech.healthcare.caretakerapp.utils;

public class Config {

    //URL to our login.php file
    public static final String SERVER_IP = "ip" ;


    public static final String TOPIC = "topic";

    //public static final String LOGIN_URL = "http://178.128.165.237//php/api/careGiverLogin.php";
    public static final String LOGIN_URL = "https://"+Config.SERVER_IP+"//php/api/careGiverLogin.php";

    public static final String CAREGIVER_REGISTER = "https://"+Config.SERVER_IP+"//php/api/smart/caregiver-register.php";

    public static final String CAREUSER_REGISTER = "https://"+Config.SERVER_IP+"//php/api/smart/registerCareuser.php";

    //public static final String LOGIN_URL = "http://"+SERVER_IP+"//php/api/careGiverLogin.php";

    public static final String ALERT_URL = "https://"+Config.SERVER_IP+"/php/api/createEventActionNote.php";
    public static final String SCAN_URL = "https://"+Config.SERVER_IP+"/php/api/smart/registerAppDevice.php";
   //public static final String ALERT_URL = "http://healthcare.iorbit-tech.com/php/api/createEventActionNote.php";
   //public static final String ALERT_URL = "http://178.128.165.237/php/api/createEventActionNote.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "subscriber";
    public static final String KEY_PASSWORD = "pass";

    public static final String KEY_ALERT = "status";
    public static final String ALERT_KEY = "eventKey";
    public static final String ALERT_UUID = "alertKey";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";
    public static final String ALERT_STATUS = "Closed";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    //public static final String SHARED_PREF_NAME = "myloginapp";
    public static final String SHARED_PREF_NAME = "iThings";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String SETTINGS_RESUME = "settings";

    public static final String SETTINGS_SHARED_PREF = "settingsin";
    public static final String DIASBLE_POP_SHARED_PREF = "popuponalert";
    public static final String DISABLE_BEEP_SHARED_PREF = "beeponalert";

    public static final String MESGTITLE_SHARED_PREF = "mesg_topic";

    public static final String MESGBODY_SHARED_PREF = "mesg_body";
    public static final String MESGRECVD_SHARED_PREF = "mesg_recvd";

    public static final String MESGRECVD_INDEX_SHARED_PREF = "mesg_recvd_index";
    public static final String MESGRECVD_TIME_SHARED_PREF = "mesg_recvd_time";
    public static final String MESGRECVD_EMAIL = "mesg_recvd_email";
    public static final String MESGRECVD_EVENTNAME  = "mesg_recvd_eventname";
    public static final String MESGRECVD_SUBSCRIBER = "mesg_recvd_subscriber";
    public static final String MESGRECVD_PHONE = "mesg_recvd_phone";
    public static final String MESGRECVD_IMG_URL = "mesg_recvd_url";

    public static final String ALERTID = "alertuuid";
    public static final String ALERTKEY = "alertkey";
    public static final String DIALOG_CLOSE = "attend";
    public static final String CURRENT_INDEX = "1";


    public static long CONST_HALFHOUR_MILLS = 30l*60*1000;
    public static long CONST_MAX_EVENT = 100;
    public static long CONST_ONEHOUR_MILLS = 60l*60*1000;

    //this is the JSON Data URL
    //make sure you are using the correct ip else it will not work
    public static final String URL_SUBSCRIBER = "https://"+Config.SERVER_IP+"/php/api/getAssignedCareUserListForCareGiver.php";

    public static final String ALERT_DELIVERED = "https://"+Config.SERVER_IP+"/php/api/eventdelivered.php";

    public static final String ALERT_OPENED = "https://"+Config.SERVER_IP+"/php/api/eventopened.php";
    //public static final String URL_SUBSCRIBER = "http://healthcare.iorbit-tech.com/php/api/getAssignedCareUserListForCareGiver.php";
    //public static final String URL_SUBSCRIBER = "http://178.128.165.237/php/api/getAssignedCareUserListForCareGiver.php";
    //public static final String URL_SUBSCRIBER = "http://healthcare.iorbit-tech.com/php/api/getAssignedCareUserListForCareGiver.php";


}