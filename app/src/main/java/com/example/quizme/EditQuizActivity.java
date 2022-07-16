package com.example.quizme;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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

public class EditQuizActivity extends AppCompatActivity {

    EditText Qid,Qa1,Qa2,Qa3,Qa4;
    RadioButton rdb1,rdb2,rdb3,rdb4;
    TextView corrAns;
    RadioGroup rg;
    int newCorrAns;
    LoadingDialog loadDialog;
    String mongoId;
    int Qnum;

    private static final int PICK_IMAGE = 100;
    //private  static  final  int PERMISSION_CODE = 1001;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

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
        setContentView(R.layout.activity_edit_quiz);

        Intent intent = getIntent();
        String quiz= intent.getExtras().getString("quiz",null);
        Qnum = intent.getExtras().getInt("Qnum",-1);
        mongoId= intent.getExtras().getString("mongoId",null);
        Log.i("Qnumgo",String.valueOf(Qnum));
        JSONArray problems =  null;
        JSONArray answers =  null;
        int correctAns = 0;
        String question=null,ans1=null,ans2=null,ans3=null,ans4=null;
        try {
            problems = new JSONArray(quiz);
            question = problems.getJSONObject(Qnum).getString("question");
            answers = problems.getJSONObject(Qnum).getJSONArray("answers");
            correctAns = problems.getJSONObject(Qnum).getInt("correctAnswer");
            ans1 = answers.get(0).toString().trim();
            ans2 = answers.get(1).toString().trim();
            ans3 = answers.get(2).toString().trim();
            ans4 = answers.get(3).toString().trim();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        rg = (RadioGroup) findViewById(R.id.correctAnswerRadioGroup);
        rdb1 = (RadioButton) findViewById(R.id.answer_radio1);
        rdb2 = (RadioButton) findViewById(R.id.answer_radio2);
        rdb3 = (RadioButton) findViewById(R.id.answer_radio3);
        rdb4 = (RadioButton) findViewById(R.id.answer_radio4);
        Qid = findViewById(R.id.qId);
        Qa1 = findViewById(R.id.qA1);
        Qa2 = findViewById(R.id.qA2);
        Qa3 = findViewById(R.id.qA3);
        Qa4 = findViewById(R.id.qA4);
        corrAns = findViewById(R.id.text_correct_answer);
        Qid.setText(question);
        Qa1.setText(ans1);
        Qa2.setText(ans2);
        Qa3.setText(ans3);
        Qa4.setText(ans4);

        if(correctAns == 1){
            rdb1.setChecked(true);
            corrAns.setText("Correct Answer = 1");
        }
        else if(correctAns == 2){
            rdb2.setChecked(true);
            corrAns.setText("Correct Answer = 2");
        } else if(correctAns == 3){
            rdb3.setChecked(true);
            corrAns.setText("Correct Answer = 3");
        }else if(correctAns == 4){
            rdb4.setChecked(true);
            corrAns.setText("Correct Answer = 4");
        }


        //select new corrected answer
        newCorrAns = correctAns;

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.answer_radio1:
                        rdb1.setChecked(true);
                        newCorrAns = 1;
                        corrAns.setText("Correct Answer = 1");
                        break;
                    case R.id.answer_radio2:
                        rdb2.setChecked(true);
                        newCorrAns = 2;
                        corrAns.setText("Correct Answer = 2");
                        break;
                    case R.id.answer_radio3:
                        rdb3.setChecked(true);
                        newCorrAns = 3;
                        corrAns.setText("Correct Answer = 3");
                        break;
                    case R.id.answer_radio4:
                        rdb4.setChecked(true);
                        newCorrAns = 4;
                        corrAns.setText("Correct Answer = 4");
                        break;
                }
            }
        });


    }

    public void subEdit(View view) {

        final String question = Qid.getText().toString().trim();
        final String answer1 = Qa1.getText().toString().trim();
        final String answer2 = Qa2.getText().toString().trim();
        final String answer3 = Qa3.getText().toString().trim();
        final String answer4 = Qa4.getText().toString().trim();
        final int corrAnswer = newCorrAns;
        Boolean check = true;

        if(question.trim().length() == 0){
            //questionTitle.requestFocus();
            Qid.setError("Question name can not be empty");
            check =  false;
        }
        if(answer1.trim().length() == 0){
            //ans1.requestFocus();
            Qa1.setError("Answer1 can not be empty");
            check =  false;
        }
        if(answer2.trim().length() == 0){
            //ans2.requestFocus();
            Qa2.setError("Answer2 can not be empty");
            check =  false;
        }
        if(answer3.trim().length() == 0){
            //ans3.requestFocus();
            Qa3.setError("Answer3 can not be empty");
            check =  false;
        }
        if(answer4.trim().length() == 0){
            // ans4.requestFocus();
            Qa4.setError("Answer4 can not be empty");
            check =  false;
        }

        if(check) {

            ArrayList answers = new ArrayList();
            answers.add(answer1);
            answers.add(answer2);
            answers.add(answer3);
            answers.add(answer4);


            JSONObject data = new JSONObject();
            JSONArray answerJson = new JSONArray(answers);

            try {
                data.put("question", question);
                data.put("answers", answerJson);
                data.put("correctAnswer", corrAnswer);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            loadDialog = new LoadingDialog(this);
            loadDialog.startLoadingDialog();
            WebRequest webReq = new WebRequest(this, loadDialog, data);
            webReq.execute();
        }

    }

    public void viewQuestions(View v){

        Intent in = new Intent(this, EditQuestionActivity.class);
        SharedPreferences pref = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        pref = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int qId=pref.getInt("globalQuizID",-1);
        in.putExtra("quizID",qId);
        this.startActivity(in);

    }

    private class WebRequest extends AsyncTask<String,String,String> {

        Context con;
        LoadingDialog ld;
        JSONObject data;

        public WebRequest(Context con, LoadingDialog ld,JSONObject data){
            this.con=con;
            this.ld=ld;
            this.data =  data;
        }


        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            MediaType Json = MediaType.parse("application/json;charset=utf-8");

            RequestBody body = RequestBody.create(data.toString(), Json);


            SharedPreferences pref = con.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String jwt = pref.getString("jwt", null);
            final String token = "Bearer " + jwt;
            String baseURL =pref.getString("baseURL",null);
            String index = String.valueOf(Qnum);
            String url = baseURL + "/quiz/update/problem?id="+mongoId +"&index="+index ;
            Request request = new Request.Builder().url(
                    url
            ).header("Authorization", token).post(body).build();

            Response response = null;
            String responseBody = null;
            JSONObject json = null;

            try {
                response = client.newCall(request).execute();
                responseBody = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response.code()==200) {

                return responseBody;

            }
            //Log.i("response",responseBody);
            //Log.i("data",data.toString());

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ld.dismissDialog();
            if(s==null){
                Toast toast=Toast.makeText(con, "Something Went Wrong Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Toast toast=Toast.makeText(con, "Question Successfully Updated", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

    }


}