package com.example.quizme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.text.DateTimePatternGenerator;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quizme.utility.NetworkChangeListener;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    LoadingDialog loadDialog;

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
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                        finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String baseURL =pref.getString("baseURL",null);
        String url = baseURL + "/quiz/find/leaderboards";
        getLeaderBoards(url);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new QuizFragment()).commit();
        }
        checkForUpdates();
        installButton90to90();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String jwt = pref.getString("jwt",null);
        if(jwt==null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_quiz:
                            selectedFragment = new QuizFragment();
                            break;
                        case R.id.nav_leaderboard:
                            selectedFragment = new LBFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };


    private void getLeaderBoards(String URL) {

        loadDialog = new LoadingDialog(MainActivity.this);
        loadDialog.startLoadingDialog();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
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
                            loadDialog.dismissDialog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void installButton90to90() {
        final AllAngleExpandableButton button = findViewById(R.id.button_expandable_90_90);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.menu, R.drawable.book, R.drawable.privacy, R.drawable.ic_baseline_logout_24};
        int[] color = {R.color.textColor, R.color.blue, R.color.green, R.color.red};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(getApplicationContext(), drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(getApplicationContext(), drawable[i], 0);
            }
            buttonData.setBackgroundColorId(getApplicationContext(), color[i]);
            buttonData.setIconPaddingDp(15);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void setListener(AllAngleExpandableButton button) {
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                switch (index) {
                    case 1: {
                        Intent intent = new Intent(getApplicationContext(), InstructionsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 3: {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("jwt", null);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default: {
                    }
                }
            }

            @Override
            public void onExpand() {
                // showToast("onExpand");
            }

            @Override
            public void onCollapse() {
                // showToast("onCollapse");
            }
        });
    }

    private void checkForUpdates() {
        Log.e("VERSION CODE",String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("VERSION NAME",String.valueOf(BuildConfig.VERSION_NAME));

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String baseURL =pref.getString("baseURL",null);
        String url = baseURL + "/all/version/find";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String newerVersion = response.getString("version");
                            Log.e("BACKEND VERSION",newerVersion);
                            String currentVersion = String.valueOf(BuildConfig.VERSION_NAME);
                            if(newerVersion.compareTo(currentVersion) > 0){
                                android.view.ContextThemeWrapper ctw = new android.view.ContextThemeWrapper(MainActivity.this,R.style.Theme_AlertDialog);
                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctw);
                                alertDialogBuilder.setTitle("Update Quiz Me");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setIcon(R.drawable.playstore1);
                                alertDialogBuilder.setMessage("Quiz Me recommends that you update to the latest version for a seamless & enhanced performance of the app.");
                                alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try{
                                            Log.e("UPDATE TRYCATCH","try");
                                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id="+getPackageName())));
                                        }
                                        catch (ActivityNotFoundException e){
                                            Log.e("UPDATE TRYCATCH","catch");
                                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                                        }
                                    }
                                });
                                alertDialogBuilder.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(request);

    }

}