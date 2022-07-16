package com.example.quizme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.quizme.utility.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderBoardActivity extends AppCompatActivity {


    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    SwipeRefreshLayout swipeRefreshLayout;
    int index;

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
        setContentView(R.layout.activity_leader_board);

        RecyclerView recyclerView = findViewById(R.id.lBRev);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_leader_board_items);
        if(swipeRefreshLayout == null){
            Log.e("SWIPE REFRESH LAYOUT","IS NULL");
        }
        else {
            Log.e("SWIPE REFRESH LAYOUT","IS NOT NULL");
        }

        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);

        LeaderBoard leaderBoard = new LeaderBoard();
        leaderBoard.setQuizName(GlobalData.getLeaderBoard(index).getQuizName());
        leaderBoard.setLeaderBoard(GlobalData.getLeaderBoard(index).getLeaderBoard());

        LeaderBoardAdapter adapter = new LeaderBoardAdapter (leaderBoard);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getLeaderBoards();
            leaderBoard.setQuizName(GlobalData.getLeaderBoard(index).getQuizName());
            leaderBoard.setLeaderBoard(GlobalData.getLeaderBoard(index).getLeaderBoard());
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void getLeaderBoards() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String baseURL =pref.getString("baseURL",null);
        String URL = baseURL + "/quiz/find/leaderboards";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String token = pref.getString("jwt", null);
        final String auth = "Bearer " + token;
        final String tkn = token;

        // Request a string response from the provided URL.
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        //Log.e("...Quizzes",response.toString());
                        //Toast.makeText(getContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        //Log.e("response",response.toString());

                        JSONObject tmpLeaderBoard;
                        String title;
                        JSONArray leaderBoards;
                        String name;
                        JSONArray tmpMarkList;
                        SingleResult[] tmpResultList;
                        LeaderBoard[] tmpLdrBoard = new LeaderBoard[response.length()];
                        LeaderBoard tmpL;


                        try {


                            for (int i = 0; i < response.length(); i++) {

                                tmpLeaderBoard = (JSONObject) response.get(i);
                                name = tmpLeaderBoard.getString("name");
                                Log.e("QuizName", name);
                                tmpMarkList = (JSONArray) tmpLeaderBoard.get("leaderboard");
                                tmpResultList = new SingleResult[tmpMarkList.length()];
                                SingleResult singleResult;
                                String user;
                                float mark;
                                JSONObject tmp;

                                for (int j = 0; j < tmpMarkList.length(); j++) {

                                    tmp = (JSONObject)tmpMarkList.get(j);
                                    tmpResultList[j] = new SingleResult(tmp.getString("name"),(float)tmp.getDouble("marks"));
                                }


                                tmpLdrBoard[i] = new LeaderBoard(name, tmpResultList);


                            }

                            GlobalData.setLeaderBoards(tmpLdrBoard);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("regError", error.getMessage());


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", auth);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        //loadDialog.dismissDialog();
    }
}