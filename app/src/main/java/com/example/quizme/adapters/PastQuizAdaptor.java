package com.example.quizme.adapters;

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

import com.example.quizme.EditQuestion;
import com.example.quizme.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PastQuizAdaptor extends RecyclerView.Adapter<PastQuizAdaptor.ViewHolder> {


    JSONArray problems,providedAns;
    View view;
    Context context;



    public PastQuizAdaptor( JSONArray problems, Context context,JSONArray providedAns) {
        this.problems = problems;
        this.context = context;
        this.providedAns = providedAns;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View singleSch = layoutInflater.inflate(R.layout.past_question, parent, false);
        ViewHolder viewHolder = new ViewHolder(singleSch);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        EditQuestion tmpQuestion = null;
        try {
            tmpQuestion = new EditQuestion(
                    problems.getJSONObject(position).get("question").toString(),
                    position,
                    problems.getJSONObject(position).getJSONArray("answers").get(0).toString(),
                    problems.getJSONObject(position).getJSONArray("answers").get(1).toString(),
                    problems.getJSONObject(position).getJSONArray("answers").get(2).toString(),
                    problems.getJSONObject(position).getJSONArray("answers").get(3).toString(),
                    (int)problems.getJSONObject(position).get("correctAnswer"),
                    problems.getJSONObject(position).get("image_url").toString(),
                    providedAns.getInt(position)

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.questionNumber.setText(String.valueOf(tmpQuestion.getQuestionNum() + 1));
        // Log.i("number",String.valueOf(tmpQuestion.getQuestionNum() + 1));
        holder.question.setText(tmpQuestion.getQuestion().trim());
        holder.answer1.setText(tmpQuestion.getAnswer1().trim());
        holder.answer2.setText(tmpQuestion.getAnswer2().trim());
        holder.answer3.setText(tmpQuestion.getAnswer3().trim());
        holder.answer4.setText(tmpQuestion.getAnswer4().trim());

        holder.ans.clearCheck();
        if(tmpQuestion.getProvidedAns()==1){
            holder.answer1.setChecked(true);
        }else if(tmpQuestion.getProvidedAns()==2){
            holder.answer2.setChecked(true);
        }else if(tmpQuestion.getProvidedAns()==3){
            holder.answer3.setChecked(true);
        }else if(tmpQuestion.getProvidedAns()==4){
            holder.answer4.setChecked(true);
        }

        holder.correctAns.setText("Correct Answer : "+String.valueOf(tmpQuestion.getCorrectAns()));
        Log.i("provided ans",String.valueOf(tmpQuestion.getProvidedAns()));
        if(tmpQuestion.getCorrectAns() == tmpQuestion.getProvidedAns()){
            holder.icon.setImageResource(R.drawable.right);
        }else{
            holder.icon.setImageResource(R.drawable.wrong);
        }


    }

    @Override
    public int getItemCount() {
        return problems.length();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionNumber;
        public TextView question,correctAns;
        public ImageView quizImage;
        public RadioButton answer1;
        public RadioButton answer2;
        public RadioButton answer3;
        public RadioButton answer4;
        public RadioGroup ans;
        public ImageView icon;

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
            this.correctAns = itemView.findViewById(R.id.correctAns);
            this.icon = itemView.findViewById(R.id.icon);

        }
    }


}