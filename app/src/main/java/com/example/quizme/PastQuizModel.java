package com.example.quizme;


import org.json.JSONArray;

public class PastQuizModel {

    String time;
    String date;
    String quizName;
    String marks;
    JSONArray problems,providedAns;

    public PastQuizModel(String time, String date, String quizName,String marks,JSONArray problems,JSONArray ans) {
        this.time = time;
        this.date = date;
        this.quizName = quizName;
        this.marks = marks;
        this.problems = problems;
        this.providedAns = ans;
    }

    public JSONArray getProvidedAns() {
        return providedAns;
    }

    public void setProvidedAns(JSONArray providedAns) {
        this.providedAns = providedAns;
    }

    public JSONArray getProblems() {
        return problems;
    }

    public void setProblems(JSONArray problems) {
        this.problems = problems;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }
}
