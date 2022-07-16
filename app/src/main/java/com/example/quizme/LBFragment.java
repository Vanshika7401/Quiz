package com.example.quizme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LBFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup lbFrag = (ViewGroup)inflater.inflate(R.layout.lb_fragment, container, false);

        RecyclerView recyclerView = lbFrag.findViewById(R.id.qNamesRev);
        swipeRefreshLayout = lbFrag.findViewById(R.id.swipe_refresh_layout_leader_board);

        ArrayList<String> names = new ArrayList<>();

        for(int i=0;i<GlobalData.getLeaderBoardLength();i++){
            names.add(GlobalData.getLeaderBoardName(i));
        }


        QuizNameAdapter adapter = new QuizNameAdapter (names);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getLeaderBoards();
            names.clear();
            for(int i=0;i<GlobalData.getLeaderBoardLength();i++){
                names.add(GlobalData.getLeaderBoardName(i));
            }
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
        return  lbFrag;



    }

    private void getLeaderBoards() {
        SharedPreferences pref = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String baseURL =pref.getString("baseURL",null);
        String URL = baseURL + "/quiz/find/leaderboards";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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