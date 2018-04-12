package com.struggleassist.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_incident);

        topQuestion = (TextView) findViewById(R.id.topQuestion);
        topYes = (CheckBox) findViewById(R.id.topYes);
        topNo = (CheckBox) findViewById(R.id.topNo);
        bottomQuestion = (TextView) findViewById(R.id.bottomQuestion);
        bottomYes = (CheckBox) findViewById(R.id.bottomYes);
        bottomNo =(CheckBox) findViewById(R.id.bottomNo);
        bFreeResponse = (EditText) findViewById(R.id.binaryFreeResponse);
        freeResponse = (EditText) findViewById(R.id.freeResponse);
        button = (Button)findViewById(R.id.button);


        pageCt = 1;
        firstCt = 0;
        secondCt = 1;

        responses = new ArrayList<>();
        questions = new ArrayList<>();
        //page #1
        questions.add(new Tuple<>("Did you feel the fall coming?", 0));
        questions.add(new Tuple<>("Did you have a headache leading into the fall?", 0));
        //page #2
        questions.add(new Tuple<>("Did you have any chest pains or shortness of breathe before or after the fall?", 0));
        questions.add(new Tuple<>("Were you unconscious at any point before, during, or after the fall?", 0));
        //page #3
        questions.add(new Tuple<>("Did something in the environment around you contribute to the fall? If so please explain", 2));
        //page #4
        questions.add(new Tuple<>("Do you have any other symptoms or notes you would like to record?", 1));

        setQuestions();
    }

    private void setQuestions(){
        if(pageCt < 3) {
            topQuestion.setText(questions.get(firstCt).getQuestion().toString());
            bottomQuestion.setText(questions.get(secondCt).getQuestion().toString());
            bFreeResponse.setVisibility(View.INVISIBLE);
            freeResponse.setVisibility(View.INVISIBLE);
            firstCt +=2;
            secondCt +=2;
            Log.d("Symptoms:", "set" + pageCt);
        }
        else{
            topQuestion.setText(questions.get(firstCt).getQuestion().toString());
            if(pageCt == 3){
                bFreeResponse.setVisibility(View.VISIBLE);
                freeResponse.setVisibility(View.INVISIBLE);
                bottomQuestion.setVisibility(View.INVISIBLE);
                bottomYes.setVisibility(View.INVISIBLE);
                bottomNo.setVisibility(View.INVISIBLE);
                Log.d("Symptoms:", "set" + pageCt);
            }
            else{
                freeResponse.setVisibility(View.VISIBLE);
                bFreeResponse.setVisibility(View.INVISIBLE);
                bottomQuestion.setVisibility(View.INVISIBLE);
                topYes.setVisibility(View.INVISIBLE);
                topNo.setVisibility(View.INVISIBLE);
                bottomYes.setVisibility(View.INVISIBLE);
                bottomNo.setVisibility(View.INVISIBLE);
                button.setText("Submit");
                Log.d("Symptoms:", "set" + pageCt);
            }
            firstCt++;
        }
        pageCt++;
    }

    public void cancelQuiz_onClick(View v){
        Intent i = new Intent();
        setResult(Activity.RESULT_CANCELED, i);
        finish();
    }

    public void button_onClick(View v) {
        Log.d("Symptoms:", "onClick: " + pageCt);
        boolean topYesSelected = topYes.isChecked();
        boolean bottomYesSelected = bottomYes.isChecked();
        boolean topNoSelected = topNo.isChecked();
        boolean bottomNoSelected = bottomNo.isChecked();
        String fr = freeResponse.getText().toString();
        String bfr = bFreeResponse.getText().toString();

        switch (pageCt - 1) {
            case 1:
                if (topYesSelected && !topNoSelected) {
                    responses.add("Felt the fall coming");
                } else if (topNoSelected && !topYesSelected) {
                    responses.add("Did not feel the fall coming");
                }
                if (bottomYesSelected && !bottomNoSelected) {
                    responses.add("Had a headache prior to the fall");
                } else if (bottomNoSelected && !bottomYesSelected) {
                    responses.add("Did not have a headache prior to the fall");
                }
                topYes.setChecked(false);
                topNo.setChecked(false);
                bottomYes.setChecked(false);
                bottomNo.setChecked(false);
                break;

            case 2:
                if (topYesSelected && !topNoSelected) {
                    responses.add("Experienced chest pains and/or shortness of breath");
                } else if (topNoSelected && !topYesSelected) {
                    responses.add("Did not experience chest pains and/or shortness of breath");
                }
                if (bottomYesSelected && !bottomNoSelected) {
                    responses.add("Was unconscious as some point in time around the fall");
                } else if (bottomNoSelected && !bottomYesSelected) {
                    responses.add("Was never unconscious");
                }
                topYes.setChecked(false);
                topNo.setChecked(false);
                bottomYes.setChecked(false);
                bottomNo.setChecked(false);
                break;

            case 3:
                if (topYesSelected) {
                    responses.add("The environment contributed to the fall: " + bfr);
                }
                topYes.setChecked(false);
                topNo.setChecked(false);
                break;

            case 4:
                responses.add(fr);
                break;

            default:
                Log.d("Symptoms:", Integer.toString(pageCt));
                break;
        }
        if (pageCt < 5)
            setQuestions();
        else {

            Intent i = new Intent();
            i.putExtra("NotesString",getResponsesAsString());
            Log.d("Symptoms:", "Why: " + getResponsesAsString());
            setResult(Activity.RESULT_OK, i);
            finish();
        }
    }

    public String getResponsesAsString(){
        StringBuilder result = new StringBuilder("");

        for(int i = 0; i < responses.size(); i++){
            result.append("-" + responses.get(i) + "\n");
        }
        //result.delete(result.length() - 2, result.length());//removes last ", "
        return result.toString();
    }

    public void topYes_Click(View v){
        topNo.setChecked(false);
    }
    public void topNo_Click(View v){
        topYes.setChecked(false);
    }
    public void bottomYes_Click(View v){
        bottomNo.setChecked(false);
    }
    public void bottomNo_Click(View v){
        bottomYes.setChecked(false);
    }
}
