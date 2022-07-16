package com.example.quizme;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class NewQuestionEditActivity extends AppCompatActivity {

    EditText questionTitle;
    EditText ans1;
    EditText ans2;
    EditText ans3;
    EditText ans4;
    RadioGroup selectAnswerSection;
    int correctAnswer = -1;
    Button submit;
    static String mongoQid;
    TextView showCorrectAnswer;

    String title;
    String answer1;
    String answer2;
    String answer3;
    String answer4;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        Intent in = getIntent();
        mongoQid = in.getExtras().getString("quizId");

        questionTitle = findViewById(R.id.qId);
        ans1 = findViewById(R.id.qA1);
        ans2 = findViewById(R.id.qA2);
        ans3 = findViewById(R.id.qA3);
        ans4 = findViewById(R.id.qA4);
        selectAnswerSection = findViewById(R.id.correctAnswerRadioGroup);
        submit = findViewById(R.id.addQuestion);
        showCorrectAnswer = findViewById(R.id.text_correct_answer);

        //mongo quiz id
        //Log.i("Mongo QID : ", mongoQid);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = questionTitle.getText().toString();
                answer1 = ans1.getText().toString();
                answer2 = ans2.getText().toString();
                answer3 = ans3.getText().toString();
                answer4 = ans4.getText().toString();

                if (title.trim().length() == 0) {
                    //questionTitle.requestFocus();
                    questionTitle.setError("Question name can not be empty");
                }
                if (answer1.trim().length() == 0) {
                    //ans1.requestFocus();
                    ans1.setError("Answer1 can not be empty");
                }
                if (answer2.trim().length() == 0) {
                    //ans2.requestFocus();
                    ans2.setError("Answer2 can not be empty");
                }
                if (answer3.trim().length() == 0) {
                    //ans3.requestFocus();
                    ans3.setError("Answer3 can not be empty");
                }
                if (answer4.trim().length() == 0) {
                    // ans4.requestFocus();
                    ans4.setError("Answer4 can not be empty");
                }
                if(correctAnswer == -1) {
                    showCorrectAnswer.setError("Select the Correct Answer");
                }

                if(title.trim().length() != 0 && answer1.trim().length() != 0 && answer2.trim().length() != 0 && answer3.trim().length() != 0 && answer4.trim().length() != 0 && correctAnswer != -1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doPostRequest();
                        }
                    }).start();
                }



            }
        });


    }


    private void doPostRequest() {
        Log.d("Okhttp3:", "doPostRequest function called");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String baseURL = pref.getString("baseURL", null);
        String url = baseURL + "/quiz/add/problems?id=" +mongoQid;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // connect timeout
                .writeTimeout(30, TimeUnit.SECONDS) // write timeout
                .readTimeout(30, TimeUnit.SECONDS) // read timeout
                .build();

        MediaType JSON = MediaType.parse("application/json;charset=utf-8");

        JSONArray questionsArray = new JSONArray();
//        for (Question question : GlobalData.getProblems()) {
//            JSONObject jo = new JSONObject();
//            try {
//                jo.put("question", question.getQuestion());
//                JSONArray ja = new JSONArray(question.getAnswers());
//                jo.put("answers", ja);
//                jo.put("correctAnswer", question.getCorrectAnswer());
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            questionsArray.put(jo);
//        }
        JSONObject jo = new JSONObject();
        ArrayList<String> answers = new ArrayList<>();
        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        answers.add(answer4);
        try {
            jo.put("question", title);
            JSONArray ja = new JSONArray(answers);
            jo.put("answers", ja);
            jo.put("correctAnswer", correctAnswer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        questionsArray.put(jo);

        JSONObject actualData = new JSONObject();

        try {
            actualData.put("addedProblems", questionsArray);
        } catch (JSONException e) {
            Log.d("Okhttp3:", "JSON Exception");
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, actualData.toString());
        Log.d("Okhttp3:", "Requestbody created");
        Log.d("Okhttp3:", "body = \n" + body.toString());
        Log.d("Okhttp3:", "actualData = \n" + actualData.toString());
        String jwt = pref.getString("jwt", null);
        Log.d("Okhttp3:", "jwt = " + jwt);
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + jwt)
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.d("Okhttp3:", "request done, got the response");
            Log.d("Okhttp3:", response.body().string());


            final String toast_message;

            if (response.code() == 200) {
                toast_message = "Question Successfully Added";
//                PopUpSubmission.quiz_link = GlobalData.getLink();

//                startActivity(new Intent(NewQuestionEditActivity.this,PopUpSubmission.class));
                if (getApplicationContext() != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), toast_message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearQuizData();
                    }
                });
            } else {
                toast_message = "Something Went Wrong";
                if (getApplicationContext() != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), toast_message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


        } catch (IOException e) {
            Log.d("Okhttp3:", "IOEXCEPTION while request");
            e.printStackTrace();
        }
    }

    public void answerCheckButton(View view){
        int radioId = selectAnswerSection.getCheckedRadioButtonId();

        View radioButton = selectAnswerSection.findViewById(radioId);
        correctAnswer = selectAnswerSection.indexOfChild(radioButton)+1;

//        Toast.makeText(this,"Selected Radio Button = "+correctAnswer,Toast.LENGTH_SHORT).show();
        String correct = "Correct Answer = "+((correctAnswer == -1)?"Not Selected":correctAnswer);
        showCorrectAnswer.setText(correct);
        showCorrectAnswer.setError(null);
    }

    private void clearQuizData() {

        questionTitle.setText("");
        ans1.setText("");
        ans2.setText("");
        ans3.setText("");
        ans4.setText("");

        correctAnswer = -1;
        String correct = "Correct Answer = "+((correctAnswer == -1)?"Not Selected":correctAnswer);
        showCorrectAnswer.setText(correct);
        selectAnswerSection.clearCheck();
        //TODO : clear image data
    }
}
