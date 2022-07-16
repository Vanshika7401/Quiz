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

public class EditQuestionActivity extends AppCompatActivity {

    EditQuestionadapter adapter;
    ViewPager2 viewPager2;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    int quizID;
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
        setContentView(R.layout.activity_edit_questions);

        Intent intent = getIntent();
        quizID = intent.getExtras().getInt("quizID");
        WebRequest webRequest = new WebRequest(this);
        webRequest.execute();


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
        FloatingActionButton home = findViewById(R.id.floating_home_button);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    public void newQuestion(View v) {

        Intent intent = new Intent(this, NewQuestionEditActivity.class);
        intent.putExtra("quizId", mongoID);
        this.startActivity(intent);

    }


    private class WebRequest extends AsyncTask<String, String, String> {

        Context con;

        public WebRequest(Context con) {
            this.con = con;
        }


        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String baseURL = pref.getString("baseURL", null);
            String url = baseURL + "/quiz/find/created/quizzes";

            String jwt = pref.getString("jwt", null);
            final String token = "Bearer " + jwt;

            OkHttpClient client = new OkHttpClient();
            MediaType Json = MediaType.parse("application/json;charset=utf-8");
            JSONObject data = new JSONObject();
            String val = "";

            RequestBody body = RequestBody.create(data.toString(), Json);

            okhttp3.Request request = new okhttp3.Request.Builder().url(
                    url
            ).header("Authorization", token).build();


            Response response = null;
            String responseBody = null;


            try {
                response = client.newCall(request).execute();
                responseBody = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.code() == 200) {

                return responseBody;

            }
            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject json = null;
            JSONArray val = null;

            if(s==null){
                Toast toast=Toast.makeText(con, "Something Went Wrong Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }

            try {
                json = new JSONObject(s);
                val = json.getJSONArray("createdQuizzes");
            } catch (Exception e) {
                e.printStackTrace();
            }


            JSONArray problems = null;
            try {
                problems = val.getJSONObject(quizID).getJSONArray("problems");
                mongoID = val.getJSONObject(quizID).getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            blankText = findViewById(R.id.blank_text);

            if(problems.length()==0){
                blankText.setVisibility(blankText.VISIBLE);
            }

            adapter = new EditQuestionadapter(problems, con, mongoID, quizID);

            viewPager2.setAdapter(adapter);


        }
    }


}