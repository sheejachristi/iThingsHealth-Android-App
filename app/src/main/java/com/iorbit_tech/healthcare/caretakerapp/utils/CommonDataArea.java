package com.iorbit_tech.healthcare.caretakerapp.utils;

import android.app.Activity;

import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.SubscriberActivity;
import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.Subscribers;

import java.util.List;

public class CommonDataArea {
    public static List<Subscribers> subscribersList;
    //private static final String _pause_string = "paused";
    //private static final String _resume_string = "resumed";

    private static String _view_lastState;
    public static boolean _from_background = true;
    public static boolean popup=true;
    public static boolean beep = false;
    public static boolean showSettingDlg =false;

    public static void view_paused(Activity activity) {
        _from_background = true;
    }

    public static void view_stopped(Activity activity) {

        //if (_view_lastState.equals(_pause_string)) {
        //if stop called and last event was pause then app is brought to background
        _from_background = true;
        //}  //if

    }

    public static void view_created(Activity activity) {




        _from_background = false;
        //_view_lastState = _resume_string;


    }

    public static void view_resumed(Activity activity) {




        _from_background = false;
        //_view_lastState = _resume_string;

    }
    public static void view_destroyed(Activity activity) {




        _from_background = true;
        //_view_lastState = _resume_string;

    }

}