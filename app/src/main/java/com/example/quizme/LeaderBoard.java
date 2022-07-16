package com.example.quizme;

import java.util.ArrayList;

public class LeaderBoard {

    private String  quizName;
    private SingleResult[] leaderBoard;

    public LeaderBoard() {

    }

    public LeaderBoard(String quizName, SingleResult[] leaderBoard) {
        this.quizName = quizName;
        this.leaderBoard = leaderBoard;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public SingleResult[] getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(SingleResult[] leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public void addMarks(SingleResult[] singleResult){

        this.leaderBoard = new SingleResult[singleResult.length];
        this.leaderBoard = singleResult;

    }

    public int getLBLength(){
        return this.leaderBoard.length;
    }

    public SingleResult getSingleResult(int position){
        return this.leaderBoard[position];
    }


}
