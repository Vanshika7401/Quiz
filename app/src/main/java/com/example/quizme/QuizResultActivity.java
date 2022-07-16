package com.example.quizme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizme.utility.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuizResultActivity extends AppCompatActivity {

    TextView marks;
    TextView totalMarks;
    Button home;
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
        setContentView(R.layout.activity_quiz_result);

        GlobalData.stopTimer();

        WebRequest webRequest = new WebRequest(getApplicationContext());
        webRequest.execute();

        home = findViewById(R.id.gotohome);
        marks = findViewById(R.id.marks);
        totalMarks = findViewById(R.id.totalMarks);

        marks.setText(String.valueOf(GlobalData.getMarks()));
        totalMarks.setText(String.valueOf(GlobalData.getLengthClient()));

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    private class WebRequest extends AsyncTask<String,String,String> {

        Context con;

        public WebRequest(Context con){
            this.con=con;
        }


        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // connect timeout
                    .writeTimeout(30, TimeUnit.SECONDS) // write timeout
                    .readTimeout(30, TimeUnit.SECONDS) // read timeout
                    .build();

            MediaType Json = MediaType.parse("application/json;charset=utf-8");
            JSONObject data = new JSONObject();
            String val = "";
            double marks = ((float)GlobalData.getMarks()/(float)GlobalData.getLengthClient())*100.0;
            int finalMarks = (int)marks;


            ArrayList<Integer> myAnswerList = new ArrayList<>();
            for(Question q: GlobalData.getClientQuestions()){
                myAnswerList.add(q.getClientAns());
            }
            JSONArray providedAnswers = new JSONArray(myAnswerList);
            try {
                data.put("providedAnswers",providedAnswers);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("data", data.toString());

            RequestBody body = RequestBody.create(data.toString(), Json);



            SharedPreferences pref = con.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String baseURL =pref.getString("baseURL",null);
            String token=pref.getString("jwt",null);

            Request request = new Request.Builder().url(
                    baseURL+"/quiz/submit?quizid="+GlobalData.getQuizId()+"&marks="+finalMarks
            ).addHeader("Authorization","Bearer "+token).post(body).build();

            Response response = null;
            String responseBody = null;
            JSONObject json = null;

            try {
                response = client.newCall(request).execute();
                responseBody = response.body().string();
                Log.e("Res",response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(response.code()==200) {

                try {
                    json = new JSONObject(responseBody);
                    val = json.toString();
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }else{
                return null;
            }

            Log.i("gyhbbyu",val);
            return val;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s==null){
                Toast toast=Toast.makeText(con, "Something Went Wrong Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Log.i("status","success");
            }



        }

    }
}