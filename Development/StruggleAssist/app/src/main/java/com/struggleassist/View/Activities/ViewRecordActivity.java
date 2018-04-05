package com.struggleassist.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.Record;
import com.struggleassist.R;
import com.struggleassist.View.Notifications.NotificationController;

public class ViewRecordActivity extends AppCompatActivity {

    Record record;

    TextView date;
    TextView location;
    TextView response;
    TextView tvNotes;

    @Override
    public void onCreate(Bundle savedInstanceStae){
        super.onCreate(savedInstanceStae);
        setContentView(R.layout.activity_view_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.editRecordToolbar);
        toolbar.setTitle("View Record");

        record = (Record) getIntent().getSerializableExtra("Record");

        date = (TextView) findViewById(R.id.tvEditRecordDate);
        location = (TextView) findViewById(R.id.tvEditRecordLocation);
        response = (TextView) findViewById(R.id.tvEditRecordResponse);
        tvNotes = (TextView) findViewById(R.id.tvRecordNotes);

        date.setText(record.getDateOfIncident());
        location.setText(record.getIncidentLocation());
        tvNotes.setText(record.getIncidentNotes());

        String tempResponse = record.getUserResponse();
        String r;
        switch(tempResponse){
            case NotificationController.CONFIRM_ACTION:
                r = "Confirmed";
                break;
            case NotificationController.TIMEOUT_ACTION:
                r = "Timed Out";
                break;
            case NotificationController.CANCEL_ACTION:
                r = "Canceled";
                break;
            default:
                r = "";
                break;
        }

        response.setText(r);


        Button bEditRecord = (Button) findViewById(R.id.bAddNotes);
        bEditRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PostIncident.class);
                startActivityForResult(i,5);
            }
        });

        Button bBack = (Button) findViewById(R.id.bRecordBack);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        if (reqCode == 5 && resultCode == RESULT_OK){
            String notes = data.getStringExtra("NotesString");

            record.setIncidentNotes(notes);

            DatabaseController db = new DatabaseController(this);
            db.open();
            db.updateRecord(record);
            db.close();

            tvNotes.setText(notes);
        }
    }
}
