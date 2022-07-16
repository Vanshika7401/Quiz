package com.example.quizme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.example.quizme.utility.NetworkChangeListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class HomeActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        int status = intent.getIntExtra("status",0);

        QuizListAdapter adapter;
        ViewPager2 viewPager2 = findViewById(R.id.singleQ);
        if(status == 0) {
            Log.i("lkhglhj",GlobalData.getProblems().toString());
            adapter = new QuizListAdapter(GlobalData.getProblems(), status,this);


        }else{


            CountDownTimer timer = new CountDownTimer(GlobalData.getQuizDuration()*60*1000,60*1000){

                @Override
                public void onTick(long l) {




                    Date endT = GlobalData.getEndTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date currentT = new Date();
                    String currentDate = dateFormat.format(currentT);
                    try {
                        Date d1 = dateFormat.parse(currentDate);
                        if(d1.compareTo(endT) ==0){
                            Intent intent = new Intent(HomeActivity.this,QuizResultActivity.class);
                            Toast.makeText(HomeActivity.this,"Quiz is over,submitting the quiz...",Toast.LENGTH_LONG).show();
                            startActivity(intent);

                            finish();

                        }




                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(HomeActivity.this,QuizResultActivity.class);
                    Toast.makeText(HomeActivity.this,"Quiz is over,submitting the quiz...",Toast.LENGTH_LONG).show();
                    startActivity(intent);

                }
            };
            GlobalData.setCountDownTimer(timer);
            timer.start();



            //creating dummy date
            /*ArrayList<Question> tmpQuestions = new ArrayList<>();
            Question tmpQuestion;
            for(int i=0;i<10;i++){
                tmpQuestion = new Question("Question "+ (i + 1),i,"answer1","answer2","answer3","answer4");
                tmpQuestions.add(tmpQuestion);
            }*/

            adapter = new QuizListAdapter(GlobalData.getClientQuestions(), status,this);

        }
        viewPager2.setAdapter(adapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        /*RecyclerView recyclerView = findViewById(R.id.reView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/


    }
}