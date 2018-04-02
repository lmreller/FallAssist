package com.struggleassist.View.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.struggleassist.R;

import java.util.ArrayList;
import java.util.List;

public class PostIncident extends AppCompatActivity {

    private class Tuple<L, R>{
        private final L question;
        private final R type;

        /*
        TYPE GUIDE
        0 = true/false
        1 = open response
        2 = open repsonse based on true/false
        */

        public Tuple(L question, R type) {
            this.question = question;
            this.type = type;
        }

        public L getQuestion() { return question; }
        public R getType() { return type; }

    }

    private List<Tuple> questions;
    private List<String> responses;
    int pageCt;
    private int firstCt;
    private int secondCt;
    private String finalRecord;

    private static final int numberOfPages = 4;

    TextView topQuestion;
    CheckBox topYes;
    CheckBox topNo;
    TextView bottomQuestion;
    CheckBox bottomYes;
    CheckBox bottomNo;

    EditText bFreeResponse;
    EditText freeResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_incident);

        topQuestion = findViewById(R.id.topQuestion);
        topYes = findViewById(R.id.topYes);
        topNo = findViewById(R.id.topNo);
        bottomQuestion = findViewById(R.id.bottomQuestion);
        bottomYes = findViewById(R.id.bottomYes);
        bottomNo = findViewById(R.id.bottomNo);
        bFreeResponse = findViewById(R.id.binaryFreeResponse);
        freeResponse = findViewById(R.id.freeResponse);

        pageCt = 0;
        firstCt = 0;
        secondCt = 1;

        questions = new ArrayList<Tuple>();
        //page #1
        questions.add(new Tuple<String, Integer>("Did you feel the fall coming?", 0));
        questions.add(new Tuple<String, Integer>("Did you have a headache leading into the fall?", 0));
        //page #2
        questions.add(new Tuple<String, Integer>("Did you have any chest pains or shortness of breathe before or after the fall?", 0));
        questions.add(new Tuple<String, Integer>("Were you unconscious at any point before, during, or after the fall?", 0));
        //page #3
        questions.add(new Tuple<String, Integer>("Did something in the environment around you contribute to the fall? If so please explain", 2));
        //page #4
        questions.add(new Tuple<String, Integer>("Do you have any other symptoms or notes you would like to record?", 1));

        topQuestion.setText(questions.get(firstCt).getQuestion().toString());
        topQuestion.setText(questions.get(secondCt).getQuestion().toString());
    }

    public void button_onClick(View v){

    }
}
