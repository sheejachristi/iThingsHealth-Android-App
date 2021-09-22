package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.util.List;

class AlertArrayAdapter extends ArrayAdapter<String> {
    private LayoutInflater layoutInflater;
    private List list;
    private Dao<AlertDetail,Integer>alert_Dao;
    public AlertArrayAdapter(Context context, int resource, List objects,
                                   Dao<AlertDetail, Integer> alert_Dao) {
        super(context, resource, objects);
        this.list = objects;
        this.alert_Dao = alert_Dao;
        layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_alertdetail, parent, false);
        if (list.get(position).getClass().isInstance(new AlertDetail())){


            final AlertDetail alert_detail = (AlertDetail) list.get(position);
            System.out.println("Date:"+alert_detail.alert_time);
            ((TextView)convertView.findViewById(R.id.alertname)).setText(alert_detail.alert_name);
            ((TextView)convertView.findViewById(R.id.alertmsg)).setText(alert_detail.alert_msg);
            ((TextView)convertView.findViewById(R.id.alerttime)).setText(alert_detail.alert_time);
            ((TextView)convertView.findViewById(R.id.alertsubscriber)).setText(alert_detail.alert_subscriber);
            /*if(alert_detail.alert_status=="0") {
                ((Button) convertView.findViewById(R.id.btn_alertstatus)).setText("Respond");
            }
            else {
                ((Button) convertView.findViewById(R.id.btn_alertstatus)).setText("Closed");
            }*/

        }
        return convertView;
    }
}