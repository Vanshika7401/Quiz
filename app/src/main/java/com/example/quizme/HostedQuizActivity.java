package com.example.quizme;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class HostedQuizActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<HostedQuizModel> HostedQuizModels;
    HostedQuizAdopter HostedQuizAdopter;
    LoadingDialog ld;
    TextView emptyText;

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

        ld = new LoadingDialog(this);
        ld.startLoadingDialog();
        WebRequest webRequest = new WebRequest(this);
        webRequest.execute();

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.hosted_quiz_activity);


    }



    private class WebRequest extends AsyncTask<String, String, String> {

        Context con;

        public WebRequest(Context con) {
            this.con = con;
        }


        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String baseURL =pref.getString("baseURL",null);
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

            ld.dismissDialog();
            Log.i("schedule",s);
            JSONObject json = null;
            JSONArray val=null;

            if(s==null){
                Toast toast=Toast.makeText(con, "Something Went Wrong Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }

            try {
                json = new JSONObject(s);
                val = json.getJSONArray("createdQuizzes");

            } catch ( Exception e) {
                e.printStackTrace();
            }

            emptyText = findViewById(R.id.blank_text);

            if(val.length() == 0){
                emptyText.setVisibility(emptyText.VISIBLE);
                return;
            }


            recyclerView = findViewById(R.id.recycler_view);

            HostedQuizModels = new ArrayList<>();

            for (int i = 0; i < val.length(); i++) {

                HostedQuizModel model = null;
                try {
                    model = new HostedQuizModel(
                            val.getJSONObject(i).getString("startTime"),
                            val.getJSONObject(i).getString("startDate"),
                            val.getJSONObject(i).getString("name"),
                            val.getJSONObject(i),
                            val.getJSONObject(i).getString("link")
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HostedQuizModels.add(model);
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(HostedQuizActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            HostedQuizAdopter = new HostedQuizAdopter(HostedQuizModels, HostedQuizActivity.this);
            recyclerView.setAdapter(HostedQuizAdopter);


        }
    }
}




