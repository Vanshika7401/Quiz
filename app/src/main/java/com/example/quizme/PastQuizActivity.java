package com.example.quizme;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.quizme.utility.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PastQuizActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PastQuizModel> pastQuizModels;
    PastQuizAdopter pastQuizAdopter;
    LoadingDialog loadDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    TextView blankText;

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

        loadDialog = new LoadingDialog(PastQuizActivity.this);
        loadDialog.startLoadingDialog();


        PastQuizActivity.WebRequest webRequest = new WebRequest(this,loadDialog);
        webRequest.execute();

        setContentView(R.layout.activity_past_quiz);


    }

    private class WebRequest extends AsyncTask<String, String, String> {

        Context con;
        LoadingDialog ld;

        public WebRequest(Context con, LoadingDialog ld){
            this.con=con;
            this.ld=ld;
        }


        @Override
        protected String doInBackground(String... strings) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String baseURL =pref.getString("baseURL",null);
            String url = baseURL + "/quiz/find/past/quizzes";

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
                Log.i("res",responseBody);
                return responseBody;
            }
            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

                JSONObject json = null;
                JSONArray val=null;
                Log.i("past",s);

                if(s==null){
                    Toast toast=Toast.makeText(con, "Something Went Wrong Try Again Later!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                try {
                    json = new JSONObject(s);
                    val = json.getJSONArray("pastQuizzes");
                } catch ( Exception e) {
                    e.printStackTrace();
                }

            blankText = findViewById(R.id.blank_text);

                if(val.length()==0){
                    blankText.setVisibility(blankText.VISIBLE);
                }

            recyclerView = findViewById(R.id.recycler_view);

            pastQuizModels = new ArrayList<>();

            for (int i = 0; i < val.length(); i++) {

                PastQuizModel model = null;
                try {
                    model = new PastQuizModel(
                            val.getJSONObject(i).getString("time"),
                            val.getJSONObject(i).getString("date"),
                            val.getJSONObject(i).getString("name"),
                            val.getJSONObject(i).getString("marks"),
                            val.getJSONObject(i).getJSONArray("problems"),
                            val.getJSONObject(i).getJSONArray("providedAnswers")
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pastQuizModels.add(model);
            }
            ld.dismissDialog();
            LinearLayoutManager layoutManager = new LinearLayoutManager(PastQuizActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            pastQuizAdopter = new PastQuizAdopter(pastQuizModels, PastQuizActivity.this);
            recyclerView.setAdapter(pastQuizAdopter);


        }
    }
}




