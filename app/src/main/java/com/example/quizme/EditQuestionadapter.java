package com.example.quizme;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EditQuestionadapter extends RecyclerView.Adapter<EditQuestionadapter.ViewHolder> {


    JSONArray problems;
    View view;
    Context context;
    String mongoId;
    int quizId;
    int Qnum;
    LoadingDialog ld;


    public EditQuestionadapter( JSONArray problems, Context context,String id,int qID) {
        this.problems = problems;
        this.context = context;
        this.mongoId = id;
        this.quizId = qID;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View singleSch = layoutInflater.inflate(R.layout.edit_single_question, parent, false);
        ViewHolder viewHolder = new ViewHolder(singleSch);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        EditQuestion tmpQuestion = null;
        try {
            Qnum = position;
            tmpQuestion = new EditQuestion(
                    problems.getJSONObject(position).get("question").toString(),
                    Qnum,
                    problems.getJSONObject(position).getJSONArray("answers").get(0).toString(),
                    problems.getJSONObject(position).getJSONArray("answers").get(1).toString(),
                    problems.getJSONObject(position).getJSONArray("answers").get(2).toString(),
                    problems.getJSONObject(position).getJSONArray("answers").get(3).toString(),
                    (int)problems.getJSONObject(position).get("correctAnswer"),
                    problems.getJSONObject(position).get("image_url").toString()

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.questionNumber.setText(String.valueOf(tmpQuestion.getQuestionNum() + 1));
        holder.question.setText(tmpQuestion.getQuestion().trim());
        holder.answer1.setText(tmpQuestion.getAnswer1().trim());
        holder.answer2.setText(tmpQuestion.getAnswer2().trim());
        holder.answer3.setText(tmpQuestion.getAnswer3().trim());
        holder.answer4.setText(tmpQuestion.getAnswer4().trim());

        if(tmpQuestion.getCorrectAns()==1){
            holder.answer1.setChecked(true);
        }else if(tmpQuestion.getCorrectAns()==2){
            holder.answer2.setChecked(true);
        }else if(tmpQuestion.getCorrectAns()==3){
            holder.answer3.setChecked(true);
        }else if(tmpQuestion.getCorrectAns()==4){
            holder.answer4.setChecked(true);
        }



        holder.modifyQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(view.getContext(), EditQuizActivity.class);
                in.putExtra("quiz",problems.toString());
                in.putExtra("mongoId",mongoId);
                in.putExtra("Qnum",position);
                Log.i("Qnume",String.valueOf(position));
                view.getContext().startActivity(in);
            }
        });

        holder.deleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ld = new LoadingDialog((Activity)context);
                ld.startLoadingDialog();
                WebRequest webRequest = new WebRequest(view.getContext(),ld);
                webRequest.execute();

            }
        });



    }

    private class WebRequest extends AsyncTask<String,String,String> {

        Context con;
        LoadingDialog ld;
        JSONObject data = new JSONObject();

        public WebRequest(Context con, LoadingDialog ld){
            this.con=con;
            this.ld=ld;

        }


        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences pref = con.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String baseURL =pref.getString("baseURL",null);

            OkHttpClient client = new OkHttpClient();
            MediaType Json = MediaType.parse("application/json;charset=utf-8");

            RequestBody body = RequestBody.create(data.toString(), Json);

            String jwt = pref.getString("jwt", null);
            final String token = "Bearer " + jwt;
            String index = String.valueOf(Qnum);
            String url = baseURL + "/quiz/delete/problem?id="+mongoId +"&index="+index ;
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
            Log.i("response",responseBody);
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
                Toast toast=Toast.makeText(con, "Question Successsfully deleted", Toast.LENGTH_SHORT);
                toast.show();
                Intent in = new Intent(view.getContext(), EditQuestionActivity.class);
                in.putExtra("quizID",quizId);
                view.getContext().startActivity(in);
            }

        }

    }




    @Override
    public int getItemCount() {
        return problems.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionNumber;
        public TextView question;
        public ImageView quizImage;
        public RadioButton answer1;
        public RadioButton answer2;
        public RadioButton answer3;
        public RadioButton answer4;
        public RadioGroup ans;
        public Button deleteQuestion;
        public Button modifyQuestion;


        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            this.questionNumber = itemView.findViewById(R.id.quizNum);
            this.question = itemView.findViewById(R.id.singleQus);
            this.quizImage = itemView.findViewById(R.id.quizImage);
            this.ans = itemView.findViewById(R.id.mcq);

            this.answer1 = itemView.findViewById(R.id.ans1);
            this.answer2 = itemView.findViewById(R.id.ans2);
            this.answer3 = itemView.findViewById(R.id.ans3);
            this.answer4 = itemView.findViewById(R.id.ans4);

            this.deleteQuestion = itemView.findViewById(R.id.delQus);
            this.modifyQuestion = itemView.findViewById(R.id.modQus);


        }
    }


}