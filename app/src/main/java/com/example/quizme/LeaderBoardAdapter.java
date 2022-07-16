package com.example.quizme;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {


    LeaderBoard leaderBoard;


    public LeaderBoardAdapter(LeaderBoard leaderBoard) {
        this.leaderBoard=leaderBoard;
        Log.e("LEADERBOARD ADAPTER","name = "+leaderBoard.getQuizName());
        Log.e("LEADERBOARD ADAPTER","list = "+leaderBoard.getLeaderBoard().length);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View singleSch = layoutInflater.inflate(R.layout.personal_marks, parent, false);
        ViewHolder viewHolder = new ViewHolder(singleSch);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        SingleResult singleResult = leaderBoard.getSingleResult(position);

        holder.name.setText(singleResult.getUser());
        holder.marks.setText(String.valueOf(singleResult.getMarks()));

    }



    @Override
    public int getItemCount() {
        return leaderBoard.getLBLength();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView marks;

        public ViewHolder(View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.participantName);
            this.marks = itemView.findViewById(R.id.participantMarks);

        }
    }


}
