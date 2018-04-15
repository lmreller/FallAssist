package com.struggleassist.Model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.struggleassist.R;
import com.struggleassist.View.Notifications.NotificationController;

import java.util.ArrayList;

public class RecordAdapter extends ArrayAdapter<Record> {

    private Activity activity;
    private ArrayList<Record> recordList;
    private static LayoutInflater inflater = null;

    public RecordAdapter(Activity activity, int textViewResourceId, ArrayList<Record> recordList){
        super(activity,textViewResourceId,recordList);
        try{
            this.activity = activity;
            this.recordList = recordList;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch(Exception e){
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.record_item_layout,null);
        }
        Record r = recordList.get(position);

        if(r != null){
            TextView tvRecordPosition = (TextView) v.findViewById(R.id.tvRecordPosition);
            TextView tvRecordDate = (TextView) v.findViewById(R.id.tvRecordDate);
            TextView tvRecordLocation = (TextView) v.findViewById(R.id.tvRecordLocation);
            TextView tvRecordResponse = (TextView) v.findViewById(R.id.tvRecordResponse);

            tvRecordPosition.setText(String.valueOf(position+1));
            tvRecordDate.setText(r.getDateOfIncident());
            tvRecordLocation.setText(r.getIncidentLocation());

            String tempResponse = r.getUserResponse();
            String response;
            switch(tempResponse){
                case NotificationController.CONFIRM_ACTION:
                    response = "Confirmed";
                    break;
                case NotificationController.TIMEOUT_ACTION:
                    response = "Timed Out";
                    break;
                case NotificationController.CANCEL_ACTION:
                    response = "Canceled";
                    break;
                default:
                    response = "";
                    break;
            }
            tvRecordResponse.setText(response);
        }

        return v;
    }

    public Record getRecord(int position){
        return recordList.get(position);
    }

}
