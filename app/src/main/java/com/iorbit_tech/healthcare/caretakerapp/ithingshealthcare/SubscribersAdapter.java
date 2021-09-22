package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

import com.iorbit_tech.healthcare.caretakerapp.utils.LogWriter;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.SubscriberViewHolder> {


    private Context mCtx;
    private List<Subscribers> subscriberList;
    public SubscriberActivity subscriberActivity;


    public SubscribersAdapter(Context mCtx, List<Subscribers> subscriberList) {
        this.mCtx = mCtx;
        this.subscriberList = subscriberList;
    }

    @Override
    public SubscriberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_row, null);
        return new SubscriberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubscriberViewHolder holder, int position) {
        Subscribers subscribers = subscriberList.get(position);

        //loading the image
        Glide.with(mCtx)
                .load(subscribers.getImage())
                .apply(new RequestOptions().placeholder(R.drawable.man).error(R.drawable.man))
                .into(holder.imageView);

        holder.textViewName.setText(subscribers.getName());
        holder.textViewPhone.setText(subscribers.getPhone());
        holder.subscribers = subscribers;
        LogWriter.writeLog("Subscriber", "SubscriberList");


    }

    @Override
    public int getItemCount() {
        return subscriberList.size();
    }

    class SubscriberViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewPhone;
        ImageView imageView;
        TextView textEvntDet, textRegDev, textRegEvent;
        Subscribers subscribers;


        public SubscriberViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            imageView = itemView.findViewById(R.id.imageView);
            textEvntDet = itemView.findViewById(R.id.eventdetails);
            textRegDev = itemView.findViewById(R.id.registerdevice);
            textRegEvent = itemView.findViewById(R.id.registerevent);
            textEvntDet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //subscriberActivity.readNextEventAfterIndex(0,subscribers.getEmail());
                    //subscriberActivity.checkLastEventReported(subscribers.getEmail(),true);
                    subscriberActivity.eventDetails(subscribers.getEmail());
                }
            });
            textRegDev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //subscriberActivity.readNextEventAfterIndex(0,subscribers.getEmail());
                    //subscriberActivity.checkLastEventReported(subscribers.getEmail(),true);
                    subscriberActivity.deviceScan(subscribers.getEmail());

                }
            });

            textRegEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //subscriberActivity.readNextEventAfterIndex(0,subscribers.getEmail());
                    //subscriberActivity.checkLastEventReported(subscribers.getEmail(),true);
                    subscriberActivity.generateEvent(subscribers.getEmail());

                }
            });
        }

    }

}
