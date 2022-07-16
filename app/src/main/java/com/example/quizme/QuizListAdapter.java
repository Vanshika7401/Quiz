package com.example.quizme;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder> {


    ArrayList<Question> questions;
    View view;
    int status;
    Context context;


    public QuizListAdapter(ArrayList<Question> questions, int status, Context context) {
        this.questions = questions;
        this.status = status;
        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View singleSch = layoutInflater.inflate(R.layout.single_question, parent, false);
        ViewHolder viewHolder = new ViewHolder(singleSch);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        Question tmpQuestion = this.questions.get(position);

        holder.questionNumber.setText(String.valueOf(tmpQuestion.getQuestionNum() + 1));
        holder.question.setText(tmpQuestion.getQuestion().trim());
        if (tmpQuestion.getImageUri() != null) {

            holder.quizImage.setVisibility(View.VISIBLE);
            holder.quizImage.setImageURI(tmpQuestion.getImageUri());
        }

        holder.answer1.setText(tmpQuestion.getAnswer1().trim());
        holder.answer2.setText(tmpQuestion.getAnswer2().trim());
        holder.answer3.setText(tmpQuestion.getAnswer3().trim());
        holder.answer4.setText(tmpQuestion.getAnswer4().trim());


        if (status == 1) {
            holder.deleteQuestion.setVisibility(View.GONE);
            holder.modifyQuestion.setVisibility(View.GONE);

            Log.e("position",String.valueOf(position));
            Log.e("gPosition",String.valueOf(getItemCount()-1));
            holder.finishQuiz.setVisibility(View.GONE);

            Log.d("QUIZ_LIST_ADAPTER ","Item count = "+(getItemCount()-1));
            Log.d("QUIZ_LIST_ADAPTER ","STATUS = "+1);
            Log.d("QUIZ_LIST_ADAPTER ","position = "+position);
            Log.d("QUIZ_LIST_ADAPTER ","globaldata length = "+GlobalData.getLengthClient());

            if(GlobalData.getClientQuestions().get(position).getClientAns() != 0){
                holder.ans.check(holder.ans.getChildAt(GlobalData.getClientQuestions().get(position).getClientAns()-1).getId());
            }
            else{
                holder.ans.clearCheck();
            }

            if (position == (getItemCount() - 1)) {

                holder.finishQuiz.setVisibility(View.VISIBLE);
                holder.finishQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view){

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
                        builder.setTitle("Finish Quiz...!");
                        builder.setMessage("Do you want to finish quiz?");
                        GlobalData.setQuizStatus(1);
                        String label = "Confirm";




                        builder.setPositiveButton(label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent;

                                intent = new Intent(view.getContext(), QuizResultActivity.class);
                                view.getContext().startActivity(intent);
                                ((Activity) view.getContext()).finish();

                            }
                        });

                        builder.show();

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                    }
                });
            }
            holder.ans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    //Log.e("selected",String.valueOf(i-2131230795));
                    int buttonId = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(buttonId);
                    int index = radioGroup.indexOfChild(radioButton);
                    Question tmpQuestion = GlobalData.getClientQuestions().get(position);
                    tmpQuestion.setClientAns(index+1);
                    questions.set(position, tmpQuestion);
                    GlobalData.modifyClientQuestion(position, tmpQuestion);

//                    int myIndex = 0;
//                    for(Question q: GlobalData.getClientQuestions()){
//                        Log.d("QUIZ_LIST_ADAPTER : ","Question "+myIndex+" "+q.toString());
//                    }
                    Log.e("POSITION",""+position);
                    Log.e("INDEX",String.valueOf(index));
                    Log.e("CORRECT",String.valueOf(GlobalData.clientQuestions.get(position).getCorrectAns()));
                    Log.e("CLIENT",String.valueOf(GlobalData.clientQuestions.get(position).getClientAns()));
                }
            });


        }
        else{

            holder.deleteQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalData.deleteQuestion(position);
                    GlobalData.reduceIndex(position);

                    Intent intent = new Intent(view.getContext(), CreateQuestionActivity.class);
                    view.getContext().startActivity(intent);
                    ((Activity) view.getContext()).finish();

                }
            });

            holder.modifyQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalData.setModifiedQuestion(questions.get(position));
                    GlobalData.deleteQuestion(position);
                    GlobalData.reduceIndex(position);

                    Intent intent = new Intent(view.getContext(), CreateQuestionActivity.class);
                    view.getContext().startActivity(intent);
                    ((Activity) view.getContext()).finish();

                }
            });

        }


    }



    @Override
    public int getItemCount() {
        Log.i("kjkgh",String.valueOf(questions.size()));
        return questions.size();
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
        public Button finishQuiz;

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

            this.finishQuiz = itemView.findViewById(R.id.finishQuiz);

        }
    }


}