package com.example.quizme;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizNameAdapter extends RecyclerView.Adapter<QuizNameAdapter.ViewHolder> {


    ArrayList<String> quizNames;
    String[] dateList;

    public QuizNameAdapter(ArrayList<String> quizNames) {
        this.quizNames = quizNames;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View singleSch = layoutInflater.inflate(R.layout.quiz_name, parent, false);
        ViewHolder viewHolder = new ViewHolder(singleSch);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.quizName.setText(quizNames.get(position));
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LeaderBoardActivity.class);
                intent.putExtra("index",position);
                view.getContext().startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return quizNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView quizName;
        public CardView open;

        public ViewHolder(View itemView) {
            super(itemView);



            this.quizName = itemView.findViewById(R.id.qName);
            this.open = itemView.findViewById(R.id.qButton);

        }
    }


}