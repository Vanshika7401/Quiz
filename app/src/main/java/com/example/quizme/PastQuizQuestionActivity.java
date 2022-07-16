package com.example.quizme;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quizme.adapters.PastQuizAdaptor;
import com.example.quizme.utility.NetworkChangeListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class  PastQuizQuestionActivity extends AppCompatActivity {

    PastQuizAdaptor adapter;
    ViewPager2 viewPager2;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    String quizProblems,providedAns;
    JSONArray problems,ans;
    String mongoID;
    TextView blankText;

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);

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
        setContentView(R.layout.past_question_activity);

        Intent intent = getIntent();
        quizProblems = intent.getExtras().getString("problems");
        providedAns = intent.getExtras().getString("providedAns");

        try {
            problems = new JSONArray(quizProblems);
            ans = new JSONArray(providedAns);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewPager2 = findViewById(R.id.singleQ);

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
        blankText = findViewById(R.id.blank_text);
        if(problems.length()==0){
            blankText.setVisibility(blankText.VISIBLE);
        }
        adapter = new PastQuizAdaptor(problems,this,ans);
        viewPager2.setAdapter(adapter);


    }

}
