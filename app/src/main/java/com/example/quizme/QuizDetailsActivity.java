package com.example.quizme;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.example.quizme.utility.NetworkChangeListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QuizDetailsActivity extends AppCompatActivity {

    private Button nextBtn;
    private static EditText name,startDate,startTime,duration;
    TextInputLayout tName,tStartDate,tStartTime,tDuration;
    DatePickerDialog.OnDateSetListener setListener;
    String quiz_name,quiz_startTime,quiz_startDate,quiz_duration;
    ImageButton calenderPicker,watch;
    int hour,minute;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);


        nextBtn = findViewById(R.id.btnQuizNext);
        name = findViewById(R.id.editText_name);
        startDate = findViewById(R.id.editText_startDate);
        startTime = findViewById(R.id.editText_startTime);
        duration = findViewById(R.id.editText_duration);

        tName = findViewById(R.id.outlinedTextField_name);
        tStartDate = findViewById(R.id.outlinedTextField_startDate);
        tStartTime = findViewById(R.id.outlinedTextField_startTime);
        tDuration = findViewById(R.id.outlinedTextField_duration);

        calenderPicker = findViewById(R.id.calender);
        watch = findViewById(R.id.watch);

        Calendar calender = Calendar.getInstance();
        final int year = calender.get(Calendar.YEAR);
        final int month = calender.get(Calendar.MONTH);
        final int day = calender.get(Calendar.DAY_OF_MONTH);

        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialize time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(QuizDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int in_hour, int in_minute) {
                        //initialize hour and minute
                        hour = in_hour;
                        minute = in_minute;
                        //initialize calender
                        Calendar calendar = Calendar.getInstance();
                        //set hour and minute
                        calendar.set(0,0,0,hour,minute);
                        //set selected time on text View
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                        startTime.setText(format1.format(calendar.getTime()));
                        quiz_startTime = startTime.getText().toString().trim();
                        tStartTime.setError(null);
                        if(quiz_startTime.length() == 0){
                            tStartTime.setError("*Start Time is Required");
                        }
                        if (quiz_startTime.length() != 0 && !quiz_startTime.matches("\\d{2}:\\d{2}")) {
                            tStartTime.setError("*Start Time wrong format");
                        }
                    }
                },24,0,false);
                //display previous selected time
                timePickerDialog.updateTime(hour,minute);
                //show dialog
                timePickerDialog.show();
            }
        });



        calenderPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(QuizDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month += 1;
                        String date = year + "-" + month + "-"+day;
                        String newDate = year +"-"+((month<10)?"0"+month:month)+"-"+((day<10)?"0"+day:day);

                        quiz_startDate = newDate;
                        startDate.setText(quiz_startDate);
                        quiz_startDate = startDate.getText().toString().trim();
                        tStartDate.setError(null);
                        if(quiz_startDate.length() == 0){
                            tStartDate.setError("*Start Date is Required");
                        }
                        if (quiz_startDate.length() != 0 && !quiz_startDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            tStartDate.setError("*Start Date wrong format");
                        }
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                quiz_name = name.getText().toString();
                if(quiz_name.trim().length() == 0){
                    tName.setError("*Quiz name is Required");
                }
                else{
                    tName.setError(null);
                }
            }
        });



        duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                quiz_duration = duration.getText().toString();
                if(quiz_duration.trim().length() == 0){
                    tDuration.setError("*Duration is Required");
                }
                else{
                    tDuration.setError(null);
                }
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get inputs
                quiz_name = name.getText().toString().trim();
                quiz_startTime = startTime.getText().toString().trim();
                quiz_startDate = startDate.getText().toString().trim();
                quiz_duration = duration.getText().toString().trim();


                if(quiz_name.length() == 0){
                    tName.setError("*Quiz name is Required");
                }
                if(quiz_startTime.length() == 0){
                    tStartTime.setError("*Start Time is Required");
                }
                if(quiz_startDate.length() == 0){
                    tStartDate.setError("*Start Date is Required");
                }
                if(quiz_duration.length() == 0){
                    tDuration.setError("*Duration is Required");
                }
                if(quiz_duration.length() == 0){
                    tDuration.setError("*Duration is Required");
                }
                if(quiz_startDate.length() == 0){
                    tStartDate.setError("*Start Date is Required");
                }
                if(quiz_startTime.length() == 0){
                    tStartTime.setError("*Start Time is Required");
                }
                if (quiz_startDate.length() != 0 && !quiz_startDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    tStartDate.setError("*Start Date wrong format");
                }
                if (quiz_startTime.length() != 0 && !quiz_startTime.matches("\\d{2}:\\d{2}")) {
                    tStartTime.setError("*Start Time wrong format");
                }

                Log.d("QUIZ: ","start = "+quiz_startTime);
                if(quiz_name.length() > 0 && quiz_startTime.length() > 0 && quiz_startDate.length() > 0 && quiz_duration.length() > 0 && quiz_startDate.matches("\\d{4}-\\d{2}-\\d{2}") && quiz_startTime.matches("\\d{2}:\\d{2}")){
                    GlobalData.setName(quiz_name);
                    GlobalData.setStartTime(quiz_startTime);
                    GlobalData.setStartDate(quiz_startDate);
                    GlobalData.setDuration(quiz_duration);

                    //create link
                    GlobalData.setLink(randomString(7));

                    Intent in = new Intent(QuizDetailsActivity.this, CreateQuestionActivity.class);
                    in.putExtra("status",0);
                    startActivity(in);
                }
            }
        });

    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    static void clearTexts(){
        name.setText("");
        startTime.setText("");
        startDate.setText("");
        duration.setText("");
    }
}